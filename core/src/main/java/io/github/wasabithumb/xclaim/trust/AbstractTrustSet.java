package io.github.wasabithumb.xclaim.trust;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.UUID;

public abstract class AbstractTrustSet extends AbstractSet<UUID> implements TrustSet {

    protected final TrustManager manager;
    protected final UUID owner;
    public AbstractTrustSet(@NotNull TrustManager manager, @NotNull UUID owner) {
        this.manager = manager;
        this.owner = owner;
    }

    protected @NotNull TrustManager manager() {
        return this.manager;
    }

    @Override
    public final @NotNull UUID owner() {
        return this.owner;
    }

    protected abstract boolean containsInternal(@NotNull UUID uuid);
    protected abstract boolean addInternal(@NotNull UUID uuid);
    protected abstract boolean removeInternal(@NotNull UUID uuid);
    protected abstract void clearInternal();

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof UUID uuid)) return false;
        return this.containsInternal(uuid);
    }

    @Override
    public boolean add(UUID uuid) {
        if (uuid == null) throw new NullPointerException("UUID is null");
        return this.addInternal(uuid);
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof UUID uuid)) return false;
        return this.removeInternal(uuid);
    }

    @Override
    public void clear() {
        this.clearInternal();
    }

}
