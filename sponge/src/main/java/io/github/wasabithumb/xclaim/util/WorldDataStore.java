package io.github.wasabithumb.xclaim.util;

import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.type.PlatformPersistentDataType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;
import org.spongepowered.plugin.PluginContainer;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * Implements PDC for worlds
 */
public final class WorldDataStore {

    private final WorldManager worlds;
    private final Path dir;
    private final Map<UUID, Data> map;
    private final StampedLock lock;
    private final Writer writer;

    @ApiStatus.Internal
    public WorldDataStore(
            @NotNull Server server,
            @NotNull PluginContainer plugin,
            @NotNull ConfigManager configManager
    ) {
        this.worlds = server.worldManager();
        this.dir = configManager.pluginConfig(plugin).directory().resolve("world-data");
        this.map = new HashMap<>();
        this.lock = new StampedLock();
        this.writer = new Writer(plugin.logger());
        this.writer.start();
        this.loadInitial();
        Sponge.eventManager().registerListeners(plugin, this);
    }

    //

    public @NotNull PlatformPersistentDataContainer get(@NotNull ServerWorld world) {
        UUID uuid = world.uniqueId();
        Data dat;

        long stamp = this.lock.readLock();
        try {
            dat = this.map.get(uuid);
            if (dat == null) {
                dat = new Data(this.writer, this.dir.resolve(uuid + ".dat"));
                stamp = this.lock.tryConvertToWriteLock(stamp);
                this.map.put(uuid, dat);
            }
        } finally {
            this.lock.unlock(stamp);
        }

        return dat;
    }

    public void close() {
        Sponge.eventManager().unregisterListeners(this);
        this.writer.close();
    }

    //

    private void loadInitial() {
        long stamp = this.lock.writeLock();
        try {
            for (ServerWorld sw : this.worlds.worlds()) {
                this.loadSingle(sw.uniqueId());
            }
        } finally {
            this.lock.unlock(stamp);
        }
    }

    @Listener
    public void onLoadWorld(@NotNull LoadWorldEvent event) {
        long stamp = this.lock.writeLock();
        try {
            this.loadSingle(event.world().uniqueId());
        } finally {
            this.lock.unlock(stamp);
        }
    }

    @Listener
    public void onUnloadWorld(@NotNull UnloadWorldEvent event) {
        Data data;
        long stamp = this.lock.writeLock();
        try {
            data = this.map.remove(event.world().uniqueId());
        } finally {
            this.lock.unlock(stamp);
        }
        if (data == null) return;
        this.writer.submit(data);
    }

    /** Must be write-locked */
    private void loadSingle(@NotNull UUID uuid) {
        Path file = this.dir.resolve(uuid + ".dat");
        if (!Files.exists(file)) return;
        try {
            Data d = new Data(this.writer, file);
            d.load();
            this.map.put(uuid, d);
        } catch (IOException e) {
            this.writer.logger.log(Level.WARN, "Failed to load world data", e);
            this.writer.logger.log(Level.WARN, "ID: {}", uuid);
        }
    }

    //

    private static final class Data implements PlatformPersistentDataContainer {

        private static final byte[] HEADER = new byte[] { 87, 68, 58, 51 };

        private final Writer writer;
        private final Path file;
        private final Map<String, Entry<?>> map = new HashMap<>();
        private final StampedLock lock = new StampedLock();
        private boolean dirty = false;

        Data(@NotNull Writer writer, @NotNull Path file) {
            this.writer = writer;
            this.file = file;
        }

        //

        void load() throws IOException {
            long stamp = this.lock.writeLock();
            try (InputStream is = Files.newInputStream(this.file, StandardOpenOption.READ);
                 DataInputStream dis = new DataInputStream(is)
            ) {
                this.map.clear();
                this.load0(dis);
                this.dirty = false;
            } finally {
                this.lock.unlock(stamp);
            }
        }

        void save() throws IOException {
            long stamp = this.lock.readLock();
            try {
                if (this.map.isEmpty()) {
                    Files.deleteIfExists(this.file);
                } else {
                    try (OutputStream os = Files.newOutputStream(
                            this.file,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                         DataOutputStream dos = new DataOutputStream(os)
                    ) {
                        this.save0(dos);
                    }
                }
                stamp = this.lock.tryConvertToWriteLock(stamp);
                this.dirty = false;
            } finally {
                this.lock.unlock(stamp);
            }
        }

        private void load0(@NotNull DataInputStream dis) throws IOException {
            byte[] header = dis.readNBytes(HEADER.length);
            if (!Arrays.equals(header, HEADER))
                throw new IOException("Malformed header");

            byte b;
            while ((b = dis.readByte()) != -1) {
                PlatformPersistentDataType<?> type = PlatformPersistentDataType.valueOf(b & 0xFF);
                this.load00(type, dis);
            }
        }

        private <T> void load00(
                @NotNull PlatformPersistentDataType<T> type,
                @NotNull DataInputStream dis
        ) throws IOException {
            String key = dis.readUTF();
            T data = type.deserialize(dis);
            this.map.put(key, new Entry<>(type, data));
        }

        private void save0(@NotNull DataOutputStream dos) throws IOException {
            dos.write(HEADER);

            for (Map.Entry<String, Entry<?>> mapEntry : this.map.entrySet())
                this.save00(mapEntry.getKey(), mapEntry.getValue(), dos);

            dos.writeByte(-1);
        }

        private <T> void save00(
                @NotNull String key,
                @NotNull Entry<T> entry,
                @NotNull DataOutputStream dos
        ) throws IOException {
            dos.writeByte(entry.type.ordinal());
            dos.writeUTF(key);
            entry.type.serialize(dos, entry.value);
        }

        boolean isDirty() {
            long stamp = this.lock.readLock();
            try {
                return this.dirty;
            } finally {
                this.lock.unlock(stamp);
            }
        }

        //

        @Override
        public @NotNull Data handle() {
            return this;
        }

        @Override
        public boolean has(@NotNull String key, @NotNull PlatformPersistentDataType<?> type) {
            long stamp = this.lock.readLock();
            try {
                return this.map.containsKey(key.toLowerCase(Locale.ROOT));
            } finally {
                this.lock.unlock(stamp);
            }
        }

        @Override
        public <T> @NotNull T get(@NotNull String key, @NotNull PlatformPersistentDataType<T> type) {
            Entry<?> entry;
            long stamp = this.lock.readLock();
            try {
                entry = this.map.get(key);
            } finally {
                this.lock.unlock(stamp);
            }
            if (entry == null) return type.fallback();
            if (entry.type.ordinal() != type.ordinal()) return type.fallback();
            return type.valueClass().cast(entry.value);
        }

        @Override
        public <T> void set(@NotNull String key, @NotNull PlatformPersistentDataType<T> type, @NotNull T value) {
            Entry<T> entry = new Entry<>(type, value);
            long stamp = this.lock.writeLock();
            try {
                Entry<?> old = this.map.put(key, entry);
                if (old != null && old.type.ordinal() == type.ordinal() && Objects.equals(old.value, value)) return;
                this.dirty = true;
            } finally {
                this.lock.unlock(stamp);
            }
            this.writer.submit(this);
        }

        @Override
        public void remove(@NotNull String key, @NotNull PlatformPersistentDataType<?> type) {
            long stamp = this.lock.writeLock();
            try {
                if (this.map.remove(key) == null) return;
                this.dirty = true;
            } finally {
                this.lock.unlock(stamp);
            }
            this.writer.submit(this);
        }

        //

        private record Entry<T>(
                @NotNull PlatformPersistentDataType<T> type,
                @NotNull T value
        ) { }

    }

    //

    private static final class Writer extends Thread {

        private final Logger logger;
        private final Queue<Data> queue = new ConcurrentLinkedQueue<>();
        private final Object mutex = new Object();
        private boolean shouldStop = false;

        Writer(@NotNull Logger logger) {
            this.logger = logger;
            this.setName("XClaim WDS Thread");
        }

        void submit(@NotNull Data data) {
            this.queue.add(data);
            synchronized (this.mutex) {
                this.mutex.notify();
            }
        }

        void close() {
            synchronized (this.mutex) {
                this.shouldStop = true;
                this.mutex.notify();
            }
            try {
                this.join();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {
            try {
                boolean cont;
                do {
                    cont = this.loop();
                } while (cont);
            } catch (InterruptedException e) {
                this.logger.log(Level.WARN, "WDSWriter was interrupted unexpectedly", e);
            }
        }

        private boolean loop() throws InterruptedException {
            Data data;
            while ((data = this.queue.poll()) != null) {
                this.save(data);
            }

            // Roughly twice per tick, to lighten load on queue and
            // roughly coincide with possible data updates
            TimeUnit.MILLISECONDS.sleep(25L);

            synchronized (this.mutex) {
                this.mutex.wait();
                if (this.shouldStop) return false;
            }

            return true;
        }

        private void save(@NotNull Data data) {
            if (!data.isDirty()) return;
            try {
                data.save();
            } catch (IOException e) {
                this.logger.log(Level.ERROR, "Failed to save world data ({})", data.file.toAbsolutePath());
            }
        }

    }

}
