package codes.wasabi.xclaim.map.util;

import it.unimi.dsi.fastutil.ints.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * I ported this from another, much more recent project of mine
 * https://github.com/WasabiThumb/enqr/blob/master/src/renderer/impl/svg.ts#L244
 */
public class BitmapTracer {

    private final Bitmap bitmap;
    private final ArrayBitmap traversed;

    public BitmapTracer(@NotNull Bitmap bitmap) {
        this.bitmap = bitmap;
        this.traversed = new ArrayBitmap(bitmap.getWidth(), bitmap.getHeight());
        for (int y=0; y < bitmap.getHeight(); y++) {
            for (int x=0; x < bitmap.getWidth(); x++) {
                if (!bitmap.getPixel(x, y)) this.traversed.setPixel(x, y);
            }
        }
    }

    public @Nullable List<List<Point>> poll() {
        List<Line> lines = this.pollLines();
        if (lines == null) return null;

        final LineGroup lineGroup = new LineGroup(lines, this.bitmap.getWidth());

        final List<List<Point>> paths = new ArrayList<>(1);
        List<Point> path;
        while ((path = lineGroup.popLoopAsPoints()) != null) paths.add(Collections.unmodifiableList(path));

        return paths;
    }

    private @Nullable List<Line> pollLines() {
        Point coord = this.pollUnprocessed();
        if (coord == null) return null;

        Queue<Point> points = new LinkedList<>();
        points.add(coord);

        List<Line> ret = new ArrayList<>(4);
        while ((coord = points.poll()) != null) {
            this.pullLines(coord.x(), coord.y(), ret, points);
        }
        return ret;
    }

    private void pullLines(int x, int y, List<Line> lines, Queue<Point> queue) {
        if (this.traversed.getPixel(x, y)) return;
        this.traversed.setPixel(x, y);

        Line[] identity = new Line[4];
        int identityCount = 0;

        int nx;
        int ny = y;

        nx = x - 1;
        if (this.bitmap.getPixel(nx, ny)) {
            queue.add(new Point(nx, ny));
        } else {
            identity[identityCount++] = new Line(x, y + 1, x, y, Line.Direction.LEFT);
        }

        nx = x + 1;
        if (this.bitmap.getPixel(nx, ny)) {
            queue.add(new Point(nx, ny));
        } else {
            identity[identityCount++] = new Line(x + 1, y, x + 1, y + 1, Line.Direction.RIGHT);
        }

        nx = x;
        ny = y - 1;
        if (this.bitmap.getPixel(nx, ny)) {
            queue.add(new Point(nx, ny));
        } else {
            identity[identityCount++] = new Line(x, y, x + 1, y, Line.Direction.UP);
        }

        ny = y + 1;
        if (this.bitmap.getPixel(nx, ny)) {
            queue.add(new Point(nx, ny));
        } else {
            identity[identityCount++] = new Line(x + 1, y + 1, x, y + 1, Line.Direction.DOWN);
        }

        if (identityCount != 0) {
            lines.addAll(Arrays.asList(identity).subList(0, identityCount));
        }
    }

    private @Nullable Point pollUnprocessed() {
        return this.traversed.firstUnset();
    }

    //

    private static class Line extends codes.wasabi.xclaim.map.util.Line {

        private final Direction direction;
        Line(Point a, Point b, Direction direction) {
            super(a, b);
            this.direction = direction;
        }

        Line(int ax, int ay, int bx, int by, Direction direction) {
            this(new Point(ax, ay), new Point(bx, by), direction);
        }

        enum Direction {
            RESERVED,
            LEFT,
            RIGHT,
            UP,
            DOWN
        }

    }

    private static class LineGroup {

        private final List<Line> lines;
        private final int width;
        private final boolean[] traversed;
        private final Int2ObjectMap<IntList> starts;
        LineGroup(List<Line> lines, int width) {
            // This fixes ID collision, don't @ me
            width <<= 2;

            this.lines = lines;
            this.width = width;
            this.traversed = new boolean[lines.size()];
            this.starts = new Int2ObjectLinkedOpenHashMap<>();

            Point start;
            for (int i=0; i < lines.size(); i++) {
                start = lines.get(i).a();
                int index = (start.y() * width) + start.x();
                this.starts.computeIfAbsent(index, (int ignored) -> new IntArrayList(1)).add(i);
            }
        }

        public @Nullable List<Point> popLoopAsPoints() {
            final List<Line> lines = this.popLoop();
            if (lines == null) return null;
            final int size = lines.size();

            return new AbstractList<Point>() {
                @Override
                public Point get(int i) {
                    return lines.get(i).a();
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }

        @Nullable List<Line> popLoop() {
            int startIndex = -1;
            for (int i=0; i < this.traversed.length; i++) {
                if (!this.traversed[i]) {
                    startIndex = i;
                    break;
                }
            }
            if (startIndex == -1) return null;
            this.traversed[startIndex] = true;

            Line.Direction lastDirection = Line.Direction.RESERVED;
            Line cur = this.lines.get(startIndex);
            List<Line> ret = new ArrayList<>(this.traversed.length - startIndex);

            int nextIndex;
            Line next;
            while (true) {
                IntList starting = this.getStartingAt(cur.b().x(), cur.b().y());
                if (starting.size() == 0) break;

                nextIndex = starting.getInt(0);
                if (nextIndex == startIndex) break;
                next = this.lines.get(nextIndex);
                this.traversed[nextIndex] = true;

                if (cur.direction != lastDirection) {
                    ret.add(cur);
                    lastDirection = cur.direction;
                }
                cur = next;
            }

            if (cur.direction != lastDirection) ret.add(cur);
            return ret;
        }

        private @NotNull IntList getStartingAt(int x, int y) {
            final int index = (y * this.width) + x;
            final IntList allStarts = this.starts.get(index);
            if (allStarts == null) return IntLists.emptyList();

            final int count = allStarts.size();
            final IntList nonTraversedStarts = new IntArrayList(count);

            int start;
            for (int i=0; i < count; i++) {
                start = allStarts.getInt(i);
                if (this.traversed[start]) continue;
                nonTraversedStarts.add(start);
            }

            return nonTraversedStarts;
        }

    }

}
