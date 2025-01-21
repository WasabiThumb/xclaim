package io.github.wasabithumb.xclaim.util.identity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Identities {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Map<UUID, Identity> OVERRIDES = new HashMap<>();
    private static final Map<UUID, UUID> REVERSE = new HashMap<>();

    //

    public static @NotNull Identity get(@NotNull Player player) {
        LOCK.readLock().lock();
        try {
            Identity ret = OVERRIDES.get(player.getUniqueId());
            if (ret == null) return Identity.real(player);
            return ret;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public static void set(@NotNull Player player, @Nullable Identity identity) {
        LOCK.writeLock().lock();
        try {
            if (identity == null || identity.isReal()) {
                Identity old = OVERRIDES.remove(player.getUniqueId());
                if (old != null) REVERSE.remove(old.uuid());
            } else {
                OVERRIDES.put(player.getUniqueId(), identity);
                REVERSE.put(identity.uuid(), player.getUniqueId());
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static @Nullable Player reverse(@NotNull UUID uuid) {
        LOCK.readLock().lock();
        try {
            UUID id = REVERSE.get(uuid);
            if (id == null) return null;
            return Bukkit.getPlayer(id);
        } finally {
            LOCK.readLock().unlock();
        }
    }

}
