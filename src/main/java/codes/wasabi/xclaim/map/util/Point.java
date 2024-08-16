package codes.wasabi.xclaim.map.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Point implements Comparable<Point> {

    private final int x;
    private final int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int x() {
        return x;
    }

    public final int y() {
        return y;
    }

    @Contract("_, _ -> new")
    public @NotNull Point sum(int x, int y) {
        return new Point(x() + x, y() + y);
    }

    @Contract("_, _ -> new")
    public @NotNull Point product(int x, int y) {
        return new Point(x() * x, y() * y);
    }

    @Contract("_ -> new")
    public @NotNull Point product(int scale) {
        return product(scale, scale);
    }

    @Override
    public int compareTo(@NotNull Point other) {
        if (x != other.x) return Double.compare(x, other.x);
        return Double.compare(y, other.y);
    }

    @Override
    public int hashCode() {
        return 31 * (31 + this.x) + this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Point) {
            Point other = (Point) obj;
            if (other.x == x) {
                if (other.y == y) {
                    return true;
                }
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ",y=" + y + "]";
    }

}
