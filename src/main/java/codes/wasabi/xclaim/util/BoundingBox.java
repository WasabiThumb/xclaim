package codes.wasabi.xclaim.util;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BoundingBox {

    public static BoundingBox of(Block a, Block b) {
        BoundingBox bb = new BoundingBox(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
        bb.expand(1d);
        return bb;
    }

    private final Vector mins;
    private final Vector maxs;

    public BoundingBox() {
        mins = new Vector();
        maxs = new Vector();
    }

    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        mins = new Vector(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2)
        );
        maxs = new Vector(
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2)
        );
    }

    public void normalize() {
        double minX = Math.min(mins.getX(), maxs.getX());
        double minY = Math.min(mins.getY(), maxs.getY());
        double minZ = Math.min(mins.getZ(), maxs.getZ());

        maxs.setX(Math.max(mins.getX(), maxs.getX()));
        maxs.setY(Math.max(mins.getY(), maxs.getY()));
        maxs.setZ(Math.max(mins.getZ(), maxs.getZ()));

        mins.setX(minX);
        mins.setY(minY);
        mins.setZ(minZ);
    }

    public Vector getMins() {
        return mins.clone();
    }

    public Vector getMaxs() {
        return maxs.clone();
    }

    public void setMins(Vector mins) {
        this.mins.copy(mins);
        normalize();
    }

    public void setMaxs(Vector maxs) {
        this.maxs.copy(maxs);
        normalize();
    }

    public boolean contains(Vector vector) {
        double vx = vector.getX();
        if (vx < mins.getX() || vx > maxs.getX()) return false;
        double vy = vector.getY();
        if (vy < mins.getY() || vy > maxs.getY()) return false;
        double vz = vector.getZ();
        return (vz >= mins.getZ() && vz <= maxs.getZ());
    }

    public void union(BoundingBox other) {
        double minX = Math.min(mins.getX(), other.mins.getX());
        mins.setX(minX);
        double minY = Math.min(mins.getY(), other.mins.getY());
        mins.setY(minY);
        double minZ = Math.min(mins.getZ(), other.mins.getZ());
        mins.setZ(minZ);

        double maxX = Math.min(maxs.getX(), other.maxs.getX());
        maxs.setX(maxX);
        double maxY = Math.min(maxs.getY(), other.maxs.getY());
        maxs.setY(maxY);
        double maxZ = Math.min(maxs.getZ(), other.maxs.getZ());
        maxs.setZ(maxZ);
    }

    private void expand(double amount) {
        maxs.add(new Vector(amount, amount, amount));
    }

}
