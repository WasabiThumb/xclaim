package io.github.wasabithumb.xclaim.util.tracer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Point(int x, int y) implements Comparable<Point> {

    @Contract("_, _ -> new")
    public @NotNull Point sum(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    @Contract("_, _ -> new")
    public @NotNull Point product(int x, int y) {
        return new Point(this.x * x, this.y * y);
    }

    @Contract("_ -> new")
    public @NotNull Point product(int scale) {
        return this.product(scale, scale);
    }

    @Override
    public int compareTo(@NotNull Point other) {
        int ret = Integer.compare(this.x, other.x);
        if (ret != 0) return ret;
        return Integer.compare(this.y, other.y);
    }

    @Override
    public @NotNull String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

}
