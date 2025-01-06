package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Block-aligned bounding box.
 */
public class BABB {

    @Contract("_ -> new")
    public static @NotNull BABB of(@NotNull ChunkReference cr) {
        int minX = cr.x << 4;
        int minY = cr.world.getMinHeight();
        int minZ = cr.z << 4;

        int maxX = minX + 16;
        int maxY = cr.world.getMaxHeight() + 1;
        int maxZ = minZ + 16;

        return new BABB(minX, minY, minZ, maxX, maxY, maxZ, false);
    }

    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;

    public BABB() {
        this.x1 = 0;
        this.y1 = 0;
        this.z1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.z2 = 0;
    }

    private BABB(int x1, int y1, int z1, int x2, int y2, int z2, boolean resize) {
        if (resize) {
            resize(x1, y1, z1, x2, y2, z2);
        } else {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
    }

    public BABB(int x1, int y1, int z1, int x2, int y2, int z2) {
        this(x1, y1, z1, x2, y2, z2, true);
    }

    public int minX() {
        return this.x1;
    }

    public int minY() {
        return this.y1;
    }

    public int minZ() {
        return this.z1;
    }

    public int maxX() {
        return this.x2;
    }

    public int maxY() {
        return this.y2;
    }

    public int maxZ() {
        return this.z2;
    }

    public void resize(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);

        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x < this.x2
                && y >= this.y1 && y < this.y2
                && z >= this.z1 && z < this.z2;
    }

    public void union(@NotNull BABB other) {
        int newMinX = Math.min(this.x1, other.x1);
        int newMinY = Math.min(this.y1, other.y1);
        int newMinZ = Math.min(this.z1, other.z1);
        int newMaxX = Math.max(this.x2, other.x2);
        int newMaxY = Math.max(this.y2, other.y2);
        int newMaxZ = Math.max(this.z2, other.z2);
        this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public boolean overlaps(@NotNull BABB other) {
        return !((other.x2 <= this.x1) ||
                (other.y2 <= this.y1) ||
                (other.z2 <= this.z1) ||
                (other.x1 >= this.x2) ||
                (other.y1 >= this.y2) ||
                (other.z1 >= this.z2)
        );
    }

}
