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
        List<Line> lines = new CopyOnWriteArrayList<>();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                boolean value = getPixel(x, y);
                if (value) {
                    if (!getPixel(x - 1, y)) lines.add(Line.of( x, y, x, y + 1 ));
                    if (!getPixel(x, y - 1)) lines.add(Line.of( x, y, x + 1, y ));
                    if (!getPixel(x + 1, y)) lines.add(Line.of( x + 1, y, x + 1, y + 1 ));
                    if (!getPixel(x, y + 1)) lines.add(Line.of( x, y + 1, x + 1, y + 1 ));
                }
            }
        }
        List<List<Point>> ret = new ArrayList<>();
        while (lines.size() > 0) {
            Line root = lines.remove(0);
            List<Point> path = new ArrayList<>();
            Point head = root.a();
            Point tail = root.b();
            path.add(head);
            path.add(tail);
            while (true) {
                boolean anyAction = false;
                for (Line l : lines) {
                    Point lhead = l.a();
                    Point ltail = l.b();
                    if (lhead.equals(tail)) {
                        path.add(ltail);
                        tail = ltail;
                        lines.remove(l);
                        anyAction = true;
                    } else if (ltail.equals(tail)) {
                        path.add(lhead);
                        tail = lhead;
                        lines.remove(l);
                        anyAction = true;
                    }
                }
                if (!anyAction) break;
            }
            if (includeAll || ret.size() < 1) {
                ret.add(path);
            } else if (ret.get(0).size() <= path.size()) {
                ret.set(0, path);
            }
        }
        if (includeAll) ret.sort(Comparator.comparingInt((List<Point> l) -> l.size()).reversed());
        return ret;
    }

}
