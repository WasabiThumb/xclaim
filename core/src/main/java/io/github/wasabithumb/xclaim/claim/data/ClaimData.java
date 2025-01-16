package io.github.wasabithumb.xclaim.claim.data;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.struct.TrustLevel;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorldManager;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

@ApiStatus.Internal
public final class ClaimData {

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    private static final int DIRTY_NAME         = 1;
    private static final int DIRTY_OWNER        = 2;
    private static final int DIRTY_CHUNKS       = 4;
    private static final int DIRTY_GLOBAL_PERMS = 8;
    private static final int DIRTY_USER_PERMS   = 16;

    private int dirtyMask;
    private final StampedLock lock;
    private final Lock freezeLock;
    private final Token token;
    private String name;
    private UUID owner;
    private final WorldReference world;
    private final Set<Long> chunks;
    private final Map<Permission, TrustLevel> globalPermissions;
    private final Map<UUID, Set<Permission>> userPermissions;

    private ClaimData(
            @NotNull Token token,
            @NotNull String name,
            @NotNull UUID owner,
            @NotNull WorldReference world,
            @NotNull Set<Long> chunks,
            @NotNull Map<Permission, TrustLevel> globalPermissions,
            @NotNull Map<UUID, Set<Permission>> userPermissions
    ) {
        this.dirtyMask = 0;
        this.lock = new StampedLock();
        this.freezeLock = new ReentrantLock();
        this.token = token;
        this.name = name;
        this.owner = owner;
        this.world = world;
        this.chunks = chunks;
        this.globalPermissions = globalPermissions;
        this.userPermissions = userPermissions;
    }

    //

    public @NotNull Token getToken() {
        return this.token;
    }

    public @NotNull String getName() {
        final long stamp = this.lock.readLock();
        try {
            return this.name;
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void setName(@NotNull String name) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                this.name = name;
                this.dirtyMask |= DIRTY_NAME;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public @NotNull UUID getOwner() {
        final long stamp = this.lock.readLock();
        try {
            return this.owner;
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void setOwner(@NotNull UUID owner) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                this.owner = owner;
                this.dirtyMask |= DIRTY_OWNER;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public @NotNull WorldReference getWorld() {
        return this.world;
    }

    public int getChunkCount() {
        final long stamp = this.lock.readLock();
        try {
            return this.chunks.size();
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public @NotNull @Unmodifiable Set<Long> getChunks() {
        final long stamp = this.lock.readLock();
        try {
            return Set.copyOf(this.chunks);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public boolean addChunk(long token) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                if (this.chunks.add(token)) {
                    this.dirtyMask |= DIRTY_CHUNKS;
                    return true;
                }
                return false;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public boolean removeChunk(long token) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                if (this.chunks.remove(token)) {
                    this.dirtyMask |= DIRTY_CHUNKS;
                    return true;
                }
                return false;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public boolean removeChunks(Set<Long> chunks) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                if (this.chunks.removeAll(chunks)) {
                    this.dirtyMask |= DIRTY_CHUNKS;
                    return true;
                }
                return false;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public boolean containsChunk(long token) {
        final long stamp = this.lock.readLock();
        try {
            return this.chunks.contains(token);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public @NotNull @Unmodifiable Map<Permission, TrustLevel> getGlobalPermissions() {
        final long stamp = this.lock.readLock();
        try {
            return Map.copyOf(this.globalPermissions);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public @NotNull TrustLevel getGlobalPermission(@NotNull Permission permission) {
        final long stamp = this.lock.readLock();
        try {
            TrustLevel tl = this.globalPermissions.get(permission);
            if (tl == null) return permission.getDefaultTrust();
            return tl;
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public boolean setGlobalPermission(@NotNull Permission permission, @NotNull TrustLevel level) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                if (level != this.globalPermissions.put(permission, level)) {
                    this.dirtyMask |= DIRTY_GLOBAL_PERMS;
                    return true;
                }
                return false;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public @NotNull @Unmodifiable Map<UUID, Set<Permission>> getUserPermissions() {
        final long stamp = this.lock.readLock();
        try {
            Map<UUID, Set<Permission>> ret = new HashMap<>();
            for (Map.Entry<UUID, Set<Permission>> entry : this.userPermissions.entrySet()) {
                ret.put(entry.getKey(), Set.copyOf(entry.getValue()));
            }
            return Collections.unmodifiableMap(ret);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public @NotNull @Unmodifiable Set<Permission> getUserPermissions(@NotNull UUID user) {
        final long stamp = this.lock.readLock();
        try {
            Set<Permission> ret = this.userPermissions.get(user);
            if (ret == null) return Collections.emptySet();
            return Set.copyOf(ret);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public boolean getUserPermission(@NotNull UUID user, @NotNull Permission permission) {
        final long stamp = this.lock.readLock();
        try {
            Set<Permission> set = this.userPermissions.get(user);
            if (set == null) return false;
            return set.contains(permission);
        } finally {
            this.lock.unlock(stamp);
        }
    }

    public void setUserPermission(@NotNull UUID user, @NotNull Permission permission, boolean value) {
        this.freezeLock.lock();
        try {
            final long stamp = this.lock.writeLock();
            try {
                Set<Permission> set = this.userPermissions.get(user);
                if (set == null) {
                    if (value) {
                        set = EnumSet.noneOf(Permission.class);
                        this.userPermissions.put(user, set);
                    } else {
                        return;
                    }
                }
                if (value) {
                    if (set.add(permission)) {
                        this.dirtyMask |= DIRTY_USER_PERMS;
                    }
                } else {
                    if (set.remove(permission)) {
                        this.dirtyMask |= DIRTY_USER_PERMS;
                        if (set.isEmpty()) this.userPermissions.remove(user);
                    }
                }
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    public boolean didUpdateName() {
        return this.checkDirtyFlag(DIRTY_NAME);
    }

    public boolean didUpdateOwner() {
        return this.checkDirtyFlag(DIRTY_OWNER);
    }

    public boolean didUpdateChunks() {
        return this.checkDirtyFlag(DIRTY_CHUNKS);
    }

    public boolean didUpdateGlobalPermissions() {
        return this.checkDirtyFlag(DIRTY_GLOBAL_PERMS);
    }

    public boolean didUpdateUserPermissions() {
        return this.checkDirtyFlag(DIRTY_USER_PERMS);
    }

    private boolean checkDirtyFlag(int flag) {
        final long stamp = this.lock.readLock();
        try {
            return (this.dirtyMask & flag) != 0;
        } finally {
            this.lock.unlock(stamp);
        }
    }

    /**
     * Call when starting a sync operation on this ClaimData. This will wait until any pending writes have
     * finished, and block future writes.
     * @see #endSync()
     */
    public void startSync() {
        this.freezeLock.lock();
    }

    /**
     * Call when finishing a sync operation on this ClaimData. This will allow writes again, as well as clear the
     * "dirty" flag that informs the didUpdate methods.
     * @see #startSync()
     */
    public void endSync() {
        try {
            final long stamp = this.lock.writeLock();
            try {
                this.dirtyMask = 0;
            } finally {
                this.lock.unlock(stamp);
            }
        } finally {
            this.freezeLock.unlock();
        }
    }

    //

    public static final class Builder {

        private Token token                                         = null;
        private String name                                         = null;
        private UUID owner                                          = null;
        private WorldReference world                                = null;
        private final Set<Long> chunks                              = new HashSet<>();
        private final Map<Permission, TrustLevel> globalPermissions = new EnumMap<>(Permission.class);
        private final Map<UUID, Set<Permission>> userPermissions    = new HashMap<>();
        private boolean built                                       = false;

        private void assertNotBuilt() {
            if (this.built)
                throw new IllegalStateException("Cannot use builder after #build() has been called");
        }

        private void assertNotNull(@NotNull String name, @Nullable Object value) {
            if (value == null)
                throw new IllegalStateException("Cannot build new ClaimData: " + name + " has not been set");
        }

        @Contract("_ -> this")
        public @NotNull Builder token(int token) {
            this.assertNotBuilt();
            this.token = new Token.Int(token);
            return this;
        }

        @ApiStatus.Obsolete
        @Contract("_ -> this")
        public @NotNull Builder token(@NotNull String token) {
            this.assertNotBuilt();
            this.token = new Token.Legacy(token);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder name(@NotNull String name) {
            this.assertNotBuilt();
            this.name = name;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder owner(@NotNull UUID owner) {
            this.assertNotBuilt();
            this.owner = owner;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder world(@NotNull UUID id) {
            this.assertNotBuilt();
            this.world = new WorldReference.ID(id);
            return this;
        }

        @ApiStatus.Obsolete
        @Contract("_ -> this")
        public @NotNull Builder world(@NotNull String name) {
            this.assertNotBuilt();
            this.world = new WorldReference.Name(name);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addChunk(long token) {
            this.assertNotBuilt();
            this.chunks.add(token);
            return this;
        }

        @Contract("_, _ -> this")
        public @NotNull Builder setGlobalPermission(@NotNull Permission permission, @NotNull TrustLevel level) {
            this.assertNotBuilt();
            this.globalPermissions.put(permission, level);
            return this;
        }

        @Contract("_, _, _ -> this")
        public @NotNull Builder setUserPermission(@NotNull UUID user, @NotNull Permission permission, boolean value) {
            this.assertNotBuilt();
            Set<Permission> set = this.userPermissions.computeIfAbsent(
                    user,
                    (UUID ignored) -> EnumSet.noneOf(Permission.class)
            );
            if (value) {
                set.add(permission);
            } else if (set.remove(permission) && set.isEmpty()) {
                this.userPermissions.remove(user);
            }
            return this;
        }

        @Contract("_, _ -> this")
        public @NotNull Builder setUserPermission(@NotNull UUID user, @NotNull Permission permission) {
            return this.setUserPermission(user, permission, true);
        }

        @Contract(" -> new")
        public @NotNull ClaimData build() {
            this.assertNotBuilt();
            this.assertNotNull("token", this.token);
            this.assertNotNull("name", this.name);
            this.assertNotNull("owner", this.owner);
            this.assertNotNull("world", this.world);
            this.built = true;
            return new ClaimData(
                    this.token,
                    this.name,
                    this.owner,
                    this.world,
                    this.chunks,
                    this.globalPermissions,
                    this.userPermissions
            );
        }

    }

    //

    public sealed interface Token {

        @NotNull Object value();

        int asInt() throws UnsupportedOperationException;

        @NotNull String asLegacy() throws UnsupportedOperationException;

        //

        record Int(int i) implements Token {

            @Override
            public @NotNull Integer value() {
                return this.i;
            }

            @Override
            public int asInt() {
                return this.i;
            }

            @Contract("-> fail")
            @Override
            public @NotNull String asLegacy() throws UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

        }

        record Legacy(@NotNull String value) implements Token {

            @Contract("-> fail")
            @Override
            public int asInt() throws UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NotNull String asLegacy() {
                return this.value;
            }

        }

    }

    public sealed interface WorldReference {

        @NotNull Object value();

        @Nullable PlatformWorld resolve(@NotNull PlatformWorldManager worlds);

        @NotNull UUID asID() throws UnsupportedOperationException;

        @NotNull String asName() throws UnsupportedOperationException;

        boolean matches(@Nullable PlatformWorld world);

        //

        record ID(@NotNull UUID value) implements WorldReference {

            @Override
            public @Nullable PlatformWorld resolve(@NotNull PlatformWorldManager worlds) {
                return worlds.getWorld(this.value);
            }

            @Override
            public @NotNull UUID asID() {
                return this.value;
            }

            @Contract("-> fail")
            @Override
            public @NotNull String asName() throws UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean matches(@Nullable PlatformWorld world) {
                if (world == null) return false;
                return this.value.equals(world.uuid());
            }

        }

        record Name(@NotNull String value) implements WorldReference {

            @Override
            public @Nullable PlatformWorld resolve(@NotNull PlatformWorldManager worlds) {
                return worlds.getWorld(this.value);
            }

            @Contract("-> fail")
            @Override
            public @NotNull UUID asID() throws UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

            @Override
            public @NotNull String asName() {
                return this.value;
            }

            @Override
            public boolean matches(@Nullable PlatformWorld world) {
                if (world == null) return false;
                return this.value.equals(world.name());
            }

        }

    }

}
