package codes.wasabi.xclaim.map.impl.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

class BluemapAPITracker {

    private final StampedLock stateLock = new StampedLock();
    private final LinkedList<Consumer<Object>> callbacks = new LinkedList<>();
    private boolean ready;
    private BlueMapAPI value;

    public BluemapAPITracker() {
        Optional<BlueMapAPI> current = BlueMapAPI.getInstance();
        if (current.isPresent()) {
            this.value = current.get();
            this.ready = true;
        } else {
            this.value = null;
            this.ready = false;
            BlueMapAPI.onEnable(this::setValue);
        }
        BlueMapAPI.onDisable(this::clearValue);
    }

    public @Nullable BlueMapAPI get() {
        long stamp = this.stateLock.readLock();
        try {
            return this.value;
        } finally {
            this.stateLock.unlock(stamp);
        }
    }

    public void with(Consumer<Object> apiConsumer) {
        long stamp = this.stateLock.readLock();
        try {
            if (this.ready) {
                apiConsumer.accept(this.value);
            } else {
                stamp = this.stateLock.tryConvertToWriteLock(stamp);
                this.callbacks.add(apiConsumer);
            }
        } finally {
            this.stateLock.unlock(stamp);
        }
    }

    protected void setValue(BlueMapAPI api) {
        long stamp = this.stateLock.writeLock();
        try {
            final boolean wasReady = this.ready;
            this.value = api;
            this.ready = true;
            if (wasReady) return;

            final List<Consumer<Object>> callbacks = new ArrayList<>(this.callbacks);
            this.callbacks.clear();
            stamp = this.stateLock.tryConvertToReadLock(stamp);

            for (Consumer<Object> callback : callbacks) {
                callback.accept(api);
            }
        } finally {
            this.stateLock.unlock(stamp);
        }
    }

    protected void clearValue(BlueMapAPI api) {
        long stamp = this.stateLock.readLock();
        try {
            if (!Objects.equals(api, this.value)) return;
            stamp = this.stateLock.tryConvertToWriteLock(stamp);
            this.value = null;
            this.ready = false;
        } finally {
            this.stateLock.unlock(stamp);
        }
    }

}
