package io.github.wasabithumb.xclaim.util;

import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
