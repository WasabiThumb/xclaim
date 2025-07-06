package io.github.wasabithumb.xclaim.claim;

import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.claim.transaction.impl.*;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.trust.TrustSet;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import io.github.wasabithumb.xclaim.util.collections.ProxySet;
import io.github.wasabithumb.xclaim.util.StringUtil;
import org.jetbrains.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private final ClaimManager manager;
    private final ClaimData data;
    private final ClaimState state;
    Claim(@NotNull ClaimManager manager, @NotNull ClaimData data, @NotNull ClaimState state) {
        this.manager = manager;
        this.data = data;
        this.state = state;
    }

    /**
     * Returns the ClaimManager that owns this Claim.
     */
    public @NotNull ClaimManager manager() {
        return this.manager;
    }

    /**
     * The raw (mutable) data of the claim. To modify a claim,
     * ClaimTransaction should be used instead.
     */
    @ApiStatus.Internal
    @NotNull ClaimData data() {
        return this.data;
    }

    /**
     * For exclusive usage by the ClaimManager.
     */
    @ApiStatus.Internal
    @NotNull ClaimState state() {
        return this.state;
    }

    public @NotNull String token() {
        ClaimData.Token token = this.data.getToken();
        if (token instanceof ClaimData.Token.Int tokenInt) {
            int value = tokenInt.i();
            return StringUtil.bytesToHex(new byte[] {
                    (byte) (value >> 24),
                    (byte) ((value >> 16) & 0xFF),
                    (byte) ((value >> 8) & 0xFF),
                    (byte) (value & 0xFF)
            });
        } else {
            return token.value().toString();
        }
    }

    @Contract("null -> false")
    public boolean matchesToken(@Nullable String token) {
        if (token == null) return false;
        ClaimData.Token own = this.data.getToken();
        if (own instanceof ClaimData.Token.Int) {
            if (token.length() != 8) return false;
            int n = 0;
            int tmp;
            for (int i=0; i < 8; i++) {
                n <<= 4;
                tmp = Character.digit(token.charAt(i), 16);
                if (tmp == -1) return false;
                n |= tmp;
            }
            return n == own.asInt();
        } else {
            return own.value().toString().equals(token);
        }
    }

    public @NotNull String name() {
        return this.data.getName();
    }

    public @NotNull PlatformUser owner() {
        return this.manager.runtime().platform().users().getUser(this.data.getOwner());
    }

    public @UnknownNullability PlatformWorld world() {
        return this.data.getWorld().resolve(this.manager.runtime().platform().worlds());
    }

    public boolean isValid() {
        this.state.lock.lock();
        try {
            return this.state.attachedName != null;
        } finally {
            this.state.lock.unlock();
        }
    }

    public int chunkCount() {
        return this.data.getChunkCount();
    }

    public @NotNull @Unmodifiable Set<ChunkReference> chunks() {
        final PlatformWorld world = this.world();
        if (world == null) return Collections.emptySet();

        Set<Long> backing = this.data.getChunks();
        Set<ChunkReference> ret = new ProxySet<>(ChunkReference.class, backing, (Long token) -> {
            final int[] coords = BitManipulation.i64i32(token);
            return new ChunkReference(world, coords[0], coords[1]);
        }, (ChunkReference cr) -> BitManipulation.i32i64(cr.x, cr.z));
        return Collections.unmodifiableSet(ret);
    }

    public boolean containsChunk(int x, int z) {
        long token = BitManipulation.i32i64(x, z);
        return this.data.containsChunk(token);
    }

    public boolean containsChunk(@NotNull ChunkReference cr) {
        if (!this.data.getWorld().matches(cr.world)) return false;
        return this.containsChunk(cr.x, cr.z);
    }

    public boolean containsChunk(@NotNull PlatformChunk chunk) {
        return this.containsChunk(ChunkReference.of(chunk));
    }

    public long minSquareDistance(@NotNull ChunkReference cr) {
        long ret = Long.MAX_VALUE;
        if (!this.data.getWorld().matches(cr.world))
            return ret;

        Set<Long> tags = this.data.getChunks();
        int[] tmp;
        long dist;
        long component;

        for (Long tag : tags) {
            tmp = BitManipulation.i64i32(tag);
            component = tmp[0] - cr.x;
            dist = (component * component);
            component = tmp[1] - cr.z;
            dist += (component * component);
            if (dist < ret) {
                ret = dist;
                if (dist == 0L) break;
            }
        }

        return ret;
    }

    public boolean checkPermission(@NotNull PlatformUser user, @NotNull Permission permission) {
        if (user.isOp()) return true;
        if (user.hasPermission(Permission.ADMIN_OVERRIDE)) return true;

        final UUID uuid = user.uuid();
        if (this.manager.runtime().rootConfig().rules().exemptOwner() && uuid.equals(this.data.getOwner())) return true;
        if (this.data.getUserPermission(uuid, permission)) return true;

        TrustLevel tl = this.data.getGlobalPermission(permission);
        switch (tl) {
            case ALL:
                return true;
            case VETERANS:
                if (user.isOffline() || !user.isPlayer()) return false;
                PlatformPlayer ply = user.asPlayer();
                long elapsed = Math.floorDiv(System.currentTimeMillis() - ply.getFirstPlayed(), 1000L);
                long required = this.manager.runtime().rootConfig().veteranTime();
                return elapsed >= required;
            case TRUSTED:
                final UUID owner = this.data.getOwner();
                final TrustSet trusted = this.manager.runtime().trust().get(owner);
                return trusted.contains(uuid);
            default:
                return false;
        }
    }

    public @NotNull TrustLevel getGlobalPermission(@NotNull Permission permission) {
        return this.data.getGlobalPermission(permission);
    }

    public @NotNull Set<Permission> getUserPermissions(@NotNull PlatformUser user) {
        return this.data.getUserPermissions(user.uuid());
    }

    @ApiStatus.Experimental
    public @NotNull Set<UUID> getUserPermissionKeys() {
        return Collections.unmodifiableSet(this.data.getUserPermissions().keySet());
    }

    public @NotNull @Unmodifiable Set<ClaimFlag> getFlags() {
        return this.data.getFlags();
    }

    /**
     * Creates a new transaction.
     * @param clazz The type of transaction to create.
     * @param user The user that is executing the transaction. If null, the console user is inferred.
     * @throws IllegalArgumentException The transaction type is abstract
     */
    public @NotNull <T extends ClaimTransaction> T createTransaction(@NotNull Class<T> clazz, @Nullable PlatformUser user) {
        if (user == null) user = this.manager.runtime().platform().users().console();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
            throw new IllegalArgumentException("Class \"" + clazz + "\" cannot be instantiated; is abstract");

        final ClaimMutationContext ctx = new ClaimMutationContext(this, user);
        T instance;
        try {
            Constructor<T> con = clazz.getConstructor(ClaimMutationContext.class);
            instance = con.newInstance(ctx);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Expectation failed", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            throw new AssertionError(
                    "Constructor for transaction class \"" + clazz.getName() + "\" raised an exception",
                    cause
            );
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "Unexpected error initializing transaction class \"" + clazz.getName() + "\"",
                    e
            );
        }

        return instance;
    }

    public @NotNull ModifyChunksClaimTransaction modifyChunks(@Nullable PlatformUser user) {
        return this.createTransaction(ModifyChunksClaimTransaction.class, user);
    }

    public @NotNull ModifyPermissionsClaimTransaction modifyPermissions(@Nullable PlatformUser user) {
        return this.createTransaction(ModifyPermissionsClaimTransaction.class, user);
    }

    public @NotNull ModifyFlagsClaimTransaction modifyFlags(@Nullable PlatformUser user) {
        return this.createTransaction(ModifyFlagsClaimTransaction.class, user);
    }

    public @NotNull TransferOwnerClaimTransaction transferOwner(@Nullable PlatformUser user) {
        return this.createTransaction(TransferOwnerClaimTransaction.class, user);
    }

    public @NotNull RenameClaimTransaction rename(@Nullable PlatformUser user) {
        return this.createTransaction(RenameClaimTransaction.class, user);
    }

    //


    @Override
    public int hashCode() {
        return this.data.getToken().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Claim other) {
            if (Objects.equals(this.data.getToken(), other.data.getToken()))
                return true;
        }
        return super.equals(obj);
    }

}
