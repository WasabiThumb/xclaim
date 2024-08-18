package codes.wasabi.xclaim.map.util;

import java.util.Objects;

public class Line {

    private final Point a;
    private final Point b;
    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public final Point a() {
        return a;
    }

    public final Point b() {
        return b;
    }

    public static Line of(int x1, int y1, int x2, int y2) {
        return new Line(new Point(x1, y1), new Point(x2, y2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Line) {
            Line other = (Line) obj;
            if (Objects.equals(a, other.a)) {
                if (Objects.equals(b, other.b)) return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "{" + this.a + " -> " + this.b + "}";
    }

}

