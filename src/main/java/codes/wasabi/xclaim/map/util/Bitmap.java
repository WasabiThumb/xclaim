package codes.wasabi.xclaim.map.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface Bitmap {

    /**
     * Get the width of the bitmap
     * @return Width
     */
    int getWidth();

    /**
     * Get the height of the bitmap
     * @return Height
     */
    int getHeight();

    /**
     * Gets a pixel on the bitmap
     * @param x The x value
     * @param y The y value
     * @return The pixel value, or false if X or Y is out of bounds
     */
    boolean getPixel(int x, int y);

    /**
     * Traces the outer edge of the shape
     * @return A list of points
     */
    default List<Point> trace() {
        List<List<Point>> lines = this.trace(false);
        if (lines.size() >= 1) {
            return lines.get(0);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Traces all edges of the shape. The longest edge (outer edge)
     * is the first edge in the list.
     * @param includeAll If false, only the outer edge is included
     * @return The list of edges
     */
    default List<List<Point>> trace(boolean includeAll) {
        BitmapTracer tracer = new BitmapTracer(this);
        List<List<Point>> ret = tracer.poll();
        if (ret == null) return Collections.emptyList();

        if (includeAll) {
            ret.sort(Comparator.comparingInt((List<Point> l) -> l.size()).reversed());

            return ret;
        } else {
            List<Point> longest = null;
            int longestSize = 0;

            int candidateSize;
            for (List<Point> candidate : ret) {
                if ((candidateSize = candidate.size()) > longestSize) {
                    longest = candidate;
                    longestSize = candidateSize;
                }
            }

            return (longest == null) ? Collections.emptyList() : Collections.singletonList(longest);
        }
    }

}
