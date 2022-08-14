package codes.wasabi.xclaim.util;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BoundingBox {

    public static BoundingBox of(Block a, Block b) {
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());

        int maxX = Math.max(a.getX(), b.getX()) + 1;
        int maxY = Math.max(a.getY(), b.getY()) + 1;
        int maxZ = Math.max(a.getZ(), b.getZ()) + 1;

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ, false);
    }

    private double x1;
    private double y1;
    private double z1;
    private double x2;
    private double y2;
    private double z2;

    public BoundingBox() {
        x1 = 0;
        y1 = 0;
        z1 = 0;
        x2 = 0;
        y2 = 0;
        z2 = 0;
    }

    private BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, boolean resize) {
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

    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(x1, y1, z1, x2, y2, z2, true);
    }

    public void resize(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);

        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public boolean contains(Vector vector) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        return x >= this.x1 && x < this.x2
                && y >= this.y1 && y < this.y2
                && z >= this.z1 && z < this.z2;
    }

    public void union(BoundingBox other) {
        double newMinX = Math.min(this.x1, other.x1);
        double newMinY = Math.min(this.y1, other.y1);
        double newMinZ = Math.min(this.z1, other.z1);
        double newMaxX = Math.max(this.x2, other.x2);
        double newMaxY = Math.max(this.y2, other.y2);
        double newMaxZ = Math.max(this.z2, other.z2);

        this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

}
