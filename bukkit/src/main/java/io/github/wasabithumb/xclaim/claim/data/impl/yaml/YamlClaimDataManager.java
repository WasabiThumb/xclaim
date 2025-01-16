package io.github.wasabithumb.xclaim.claim.data.impl.yaml;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.struct.TrustLevel;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class YamlClaimDataManager implements ClaimDataManager {

    private final File file;
    private final FileConfiguration yaml;
    private final Random rand;
    private final ReadWriteLock lock;
    public YamlClaimDataManager(@NotNull File file, @NotNull FileConfiguration yaml) {
        this.file = file;
        this.yaml = yaml;
        this.rand = new SecureRandom();
        this.lock = new ReentrantReadWriteLock();
    }

    @Contract("_, true -> !null")
    private @Nullable ConfigurationSection getSection(@NotNull ClaimData.Token token, boolean create) {
        String key = token.asLegacy();
        if (this.yaml.contains(key)) {
            ConfigurationSection ret = this.yaml.getConfigurationSection(key);
            if (ret != null) return ret;
        }
        if (create) this.yaml.createSection(key);
        return null;
    }

    @Override
    public @NotNull @Unmodifiable Set<ClaimData.Token> keys() {
        this.lock.readLock().lock();
        try {
            Set<String> base = this.yaml.getKeys(false);
            return base.stream()
                    .map(ClaimData.Token.Legacy::new)
                    .collect(Collectors.toSet());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public @NotNull ClaimData create(@NotNull String name, @NotNull UUID owner, @NotNull PlatformWorld world) {
        byte[] tokenData = new byte[32];
        this.rand.nextBytes(tokenData);
        final String token = StringUtil.bytesToHex(tokenData);

        this.lock.writeLock().lock();
        try {
            final ConfigurationSection section = this.getSection(new ClaimData.Token.Legacy(token), true);
            section.set("name", name);
            section.set("owner", owner.toString());
            section.set("world", world.name());
            section.createSection("chunks");
            section.createSection("permissions");
            section.createSection("users");
        } finally {
            this.lock.writeLock().unlock();
        }

        return ClaimData.builder()
                .token(token)
                .name(name)
                .owner(owner)
                .world(world.name())
                .build();
    }

    @Override
    public @Nullable ClaimData load(@NotNull ClaimData.Token key) {
        this.lock.readLock().lock();
        try {
            final ConfigurationSection section = this.getSection(key, false);
            if (section == null) return null;

            ClaimData.Builder builder = ClaimData.builder()
                    .name(Objects.requireNonNull(section.getString("name")))
                    .owner(UUID.fromString(Objects.requireNonNull(section.getString("owner"))))
                    .world(Objects.requireNonNull(section.getString("world")));

            ConfigurationSection chunks = section.getConfigurationSection("chunks");
            if (chunks != null) {
                ConfigurationSection chunk;
                for (String k : chunks.getKeys(false)) {
                    chunk = chunks.getConfigurationSection(k);
                    if (chunk == null) continue;

                    long token = BitManipulation.i32i64(
                            chunk.getInt("x", 0),
                            chunk.getInt("z", 0)
                    );
                    builder.addChunk(token);
                }
            }

            ConfigurationSection permissions = section.getConfigurationSection("permissions");
            if (permissions != null) {
                for (String k : permissions.getKeys(false)) {
                    Permission p;
                    try {
                        p = Permission.valueOf(k);
                    } catch (IllegalArgumentException ignored) {
                        continue;
                    }
                    TrustLevel tl;
                    try {
                        tl = TrustLevel.valueOf(permissions.getString(k));
                    } catch (IllegalArgumentException | NullPointerException ignored) {
                        continue;
                    }
                    builder.setGlobalPermission(p, tl);
                }
            }

            ConfigurationSection users = section.getConfigurationSection("users");
            if (users != null) {
                List<?> list;
                for (String k : users.getKeys(false)) {
                    UUID user;
                    try {
                        user = UUID.fromString(k);
                    } catch (IllegalArgumentException ignored) {
                        continue;
                    }
                    list = users.getList(k);
                    if (list == null) continue;
                    for (Object entry : list) {
                        if (!(entry instanceof String str)) continue;
                        Permission p;
                        try {
                            p = Permission.valueOf(str);
                        } catch (IllegalArgumentException ignored) {
                            continue;
                        }
                        builder.setUserPermission(user, p);
                    }
                }
            }

            return builder.build();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void queueSync(@NotNull ClaimData data) {
        this.lock.writeLock().lock();
        try {
            data.startSync();
            try {
                ConfigurationSection section = this.getSection(data.getToken(), false);
                if (section == null) return;
                this.doSync(data, section);
            } finally {
                data.endSync();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void doSync(@NotNull ClaimData data, @NotNull ConfigurationSection section) {
        if (data.didUpdateName())
            section.set("name", data.getName());
        if (data.didUpdateOwner())
            section.set("owner", data.getOwner().toString());
        if (data.didUpdateChunks())
            this.doSyncChunks(data, section);
        if (data.didUpdateGlobalPermissions())
            this.doSyncPermissions(data, section);
        if (data.didUpdateUserPermissions())
            this.doSyncUsers(data, section);
    }

    private void doSyncChunks(@NotNull ClaimData data, @NotNull ConfigurationSection section) {
        ConfigurationSection chunks = section.getConfigurationSection("chunks");
        if (chunks == null) chunks = section.createSection("chunks");

        int[] tmp;
        int head = 0;
        ConfigurationSection chunk;
        for (Long token : data.getChunks()) {
            tmp = BitManipulation.i64i32(token);
            chunk = chunks.createSection(Integer.toString(head++));
            chunk.set("x", tmp[0]);
            chunk.set("z", tmp[1]);
        }
    }

    private void doSyncPermissions(@NotNull ClaimData data, @NotNull ConfigurationSection section) {
        ConfigurationSection perms = section.getConfigurationSection("permissions");
        if (perms == null) perms = section.createSection("permissions");

        for (Map.Entry<Permission, TrustLevel> entry : data.getGlobalPermissions().entrySet()) {
            perms.set(entry.getKey().name(), entry.getValue().name());
        }
    }

    private void doSyncUsers(@NotNull ClaimData data, @NotNull ConfigurationSection section) {
        ConfigurationSection users = section.getConfigurationSection("users");
        if (users == null) users = section.createSection("users");

        for (Map.Entry<UUID, Set<Permission>> entry : data.getUserPermissions().entrySet()) {
            Set<Permission> set = entry.getValue();
            List<String> list = new ArrayList<>(set.size());
            for (Permission perm : set) list.add(perm.name());
            users.set(entry.getValue().toString(), list);
        }
    }

    @Override
    public void queueDrop(@NotNull ClaimData data) {
        this.lock.writeLock().lock();
        try {
            this.yaml.set(data.getToken().asLegacy(), null);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.yaml.save(this.file);
    }

}
