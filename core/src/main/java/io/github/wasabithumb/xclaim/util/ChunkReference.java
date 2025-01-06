package io.github.wasabithumb.xclaim.util;

import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ChunkReference {

    public static @NotNull ChunkReference of(@NotNull PlatformChunk chunk) {
        return new ChunkReference(chunk.world(), chunk.x(), chunk.z());
    }

    public static @NotNull ChunkReference of(@NotNull PlatformLocation location) {
        return new ChunkReference(
                location.world(),
                location.blockX() >> 4,
                location.blockZ() >> 4
        );
    }

    public static int compare(@Nullable Object a, @Nullable Object b) {
        final int at = (a == null) ? 3 : ((a instanceof ChunkReference) ? 0 : ((a instanceof PlatformChunk) ? 1 : 2));
        final int bt = (b == null) ? 3 : ((b instanceof ChunkReference) ? 0 : ((b instanceof PlatformChunk) ? 1 : 2));

        if (at > 1) {
            return at - bt;
        } else if (bt > 1) {
            return bt - at;
        }

        final ChunkReference ar = (at == 0) ? ((ChunkReference) a) : ChunkReference.of((PlatformChunk) a);
        final ChunkReference br = (bt == 0) ? ((ChunkReference) b) : ChunkReference.of((PlatformChunk) b);
        int tmp;

        tmp = ar.world.uuid().compareTo(br.world.uuid());
        if (tmp != 0) return tmp;
        tmp = Integer.compare(ar.x, br.x);
        if (tmp != 0) return tmp;
        tmp = Integer.compare(ar.z, br.z);
        return tmp;
    }

    public final PlatformWorld world;
    public final int x;
    public final int z;
    public ChunkReference(@NotNull PlatformWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public @NotNull PlatformChunk toChunk() {
        return this.world.getChunk(this.x, this.z);
    }

    public int getCenterBlockX() {
        return (this.x << 4) | 8;
    }

    public int getCenterBlockZ() {
        return (this.z << 4) | 8;
    }

    public @NotNull ChunkReference getRelative(int mx, int mz) {
        return new ChunkReference(this.world, this.x + mx, this.z + mz);
    }

    public @NotNull PlatformLocation getLocation(double x, double y, double z) {
        return new PlatformLocation(
                this.world,
                (this.x << 4) + x,
                y,
                (this.z << 4) + z
        );
    }

    public @NotNull BABB getBounds() {
        return BABB.of(this);
    }

    @Contract("null -> false")
    public boolean matches(PlatformChunk chunk) {
        if (chunk == null) return false;
        if (chunk.x() != this.x) return false;
        if (chunk.z() != this.z) return false;
        return Objects.equals(this.world.uuid(), chunk.world().uuid());
    }

    @Contract("null -> false")
    public boolean matches(ChunkReference other) {
        if (other == null) return false;
        return Objects.equals(this.world.uuid(), other.world.uuid())
                && this.x == other.x
                && this.z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world.uuid(), this.x, this.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof ChunkReference) {
            if (this.matches((ChunkReference) obj)) return true;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ChunkReference[world=" + this.world.name() + ", x=" + this.x + ", z=" + this.z + "]";
    }

}
