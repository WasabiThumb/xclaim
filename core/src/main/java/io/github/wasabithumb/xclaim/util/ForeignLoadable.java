package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

@ApiStatus.Internal
public class ForeignLoadable<T> {

    private final StampedLock lock = new StampedLock();
    private final List<Consumer<T>> callbacks = new LinkedList<>();
    private boolean ready;
    private T value;

    protected ForeignLoadable() {
        Optional<T> instant = this.instantValue();
        if (instant.isPresent()) {
            this.value = instant.get();
            this.ready = true;
        } else {
            this.value = null;
            this.ready = false;
            this.bindEnableListener();
        }
        this.bindDisableListener();
    }

    //

    protected @NotNull Optional<T> instantValue() {
        return Optional.empty();
    }

    protected void bindEnableListener() {
    }

    protected void bindDisableListener() {
    }

    //

    public @Nullable T get() {
        long stamp = this.lock.readLock();
        try {
            return this.value;
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void with(@NotNull Consumer<T> consumer) {
        long stamp = this.lock.readLock();
        try {
            if (this.ready) {
                consumer.accept(this.value);
            } else {
                stamp = this.lock.tryConvertToWriteLock(stamp);
                this.callbacks.add(consumer);
            }
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void setEnabled(@NotNull T value) {
        long stamp = this.lock.writeLock();
        try {
            final boolean wasReady = this.ready;
            this.value = value;
            this.ready = true;
            if (wasReady) return;

            final List<Consumer<T>> callbacks = List.copyOf(this.callbacks);
            this.callbacks.clear();
            stamp = this.lock.tryConvertToReadLock(stamp);

            for (Consumer<T> callback : callbacks)
                callback.accept(value);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void setDisabled(@NotNull T value) {
        long stamp = this.lock.readLock();
        try {
            if (!this.ready) return;
            if (!value.equals(this.value)) return;
            stamp = this.lock.tryConvertToWriteLock(stamp);
            this.value = null;
            this.ready = false;
        } finally {
            this.lock.unlock(stamp);
        }
    }

}
