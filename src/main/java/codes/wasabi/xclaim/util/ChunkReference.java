package codes.wasabi.xclaim.util;

import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChunkReference {

    public static @NotNull ChunkReference ofChunk(@NotNull Chunk chunk) {
        return new ChunkReference(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static @NotNull ChunkReference of(@NotNull Location location) {
        return new ChunkReference(
                location.getWorld(),
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4
        );
    }

    public final World world;
    public final int x;
    public final int z;
    public ChunkReference(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public Chunk toChunk() {
        return this.world.getChunkAt(this.x, this.z);
    }

    public @NotNull ChunkReference getRelative(int mx, int mz) {
        return new ChunkReference(this.world, this.x + mx, this.z + mz);
    }

    public Location getLocation(double x, double y, double z) {
        return new Location(
                this.world,
                (this.x << 4) + x,
                y,
                (this.z << 4) + z
        );
    }

    public BoundingBox getBounds() {
        World w = this.world;
        Vector a = this.getLocation(0, Platform.get().getWorldMinHeight(w), 0).toVector();
        Vector b = this.getLocation(15, w.getMaxHeight() - 1, 15).toVector();

        return new BoundingBox(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()),
                Math.max(a.getX(), b.getX()) + 1,
                Math.max(a.getY(), b.getY()) + 1,
                Math.max(a.getZ(), b.getZ()) + 1
        );
    }

    public boolean matches(Chunk chunk) {
        if (chunk.getX() != this.x) return false;
        if (chunk.getZ() != this.z) return false;
        return Objects.equals(this.world.getUID(), chunk.getWorld().getUID());
    }

    public boolean matches(ChunkReference other) {
        return Objects.equals(this.world.getUID(), other.world.getUID())
                && this.x == other.x
                && this.z == other.z;
    }

    public int getCenterBlockX() {
        return (this.x << 4) | 8;
    }

    public int getCenterBlockZ() {
        return (this.z << 4) | 8;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world.getUID(), this.x, this.z);
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
        return "ChunkReference[world=" + world.getName() + ", x=" + x + ", z=" + z + "]";
    }

}
