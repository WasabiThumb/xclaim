package io.github.wasabithumb.xclaim.trust;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public final class DirectTrustSet extends AbstractTrustSet {

    private final Set<UUID> backing;
    public DirectTrustSet(@NotNull TrustManager manager, @NotNull UUID owner, @NotNull Set<UUID> backing) {
        super(manager, owner);
        this.backing = backing;
    }

    @Override
    public @NotNull Iterator<UUID> iterator() {
        return this.backing.iterator();
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    protected boolean containsInternal(@NotNull UUID uuid) {
        return this.backing.contains(uuid);
    }

    @Override
    protected boolean addInternal(@NotNull UUID uuid) {
        if (this.backing.add(uuid)) {
            this.manager.trust(this.owner, uuid);
            return true;
        }
        return false;
    }

    @Override
    protected boolean removeInternal(@NotNull UUID uuid) {
        if (this.backing.remove(uuid)) {
            this.manager.untrust(this.owner, uuid);
            return true;
        }
        return false;
    }

    @Override
    protected void clearInternal() {
        Set<UUID> copy = new HashSet<>(this);
        this.backing.clear();
        for (UUID uuid : copy) this.manager.untrust(this.owner, uuid);
    }

}
