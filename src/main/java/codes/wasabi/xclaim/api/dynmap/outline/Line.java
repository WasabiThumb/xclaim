package codes.wasabi.xclaim.api.dynmap.outline;

public record Line(Point a, Point b) {

    public static Line of(int x1, int y1, int x2, int y2) {
        return new Line(new Point(x1, y1), new Point(x2, y2));
    }

}

