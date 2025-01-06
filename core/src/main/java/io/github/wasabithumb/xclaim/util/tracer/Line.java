package io.github.wasabithumb.xclaim.util.tracer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

class Line {

    @Contract("_, _, _, _ -> new")
    public static @NotNull Line of(int x1, int y1, int x2, int y2) {
        return new Line(new Point(x1, y1), new Point(x2, y2));
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull Line of(int x1, int y1, int x2, int y2, @NotNull Direction direction) {
        return new Line(new Point(x1, y1), new Point(x2, y2), direction);
    }

    //

    private final Point a;
    private final Point b;
    private final Direction d;
    public Line(@NotNull Point a, @NotNull Point b, @NotNull Direction direction) {
        this.a = a;
        this.b = b;
        this.d = direction;
    }

    public Line(@NotNull Point a, @NotNull Point b) {
        this(a, b, Direction.UNKNOWN);
    }

    //

    public final @NotNull Point a() {
        return this.a;
    }

    public final @NotNull Point b() {
        return this.b;
    }

    public final @NotNull Direction direction() {
        return this.d;
    }

    @Override
    public String toString() {
        return "{" + this.a + " -> " + this.b + "}";
    }

    //

    public enum Direction {
        UNKNOWN,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

}

