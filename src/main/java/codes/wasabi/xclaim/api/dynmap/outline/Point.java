package codes.wasabi.xclaim.api.dynmap.outline;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Point(int x, int y) implements Comparable<Point> {

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

}
