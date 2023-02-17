package codes.wasabi.xclaim.map.util;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Point> ret = Collections.emptyList();
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
            if (path.size() > ret.size()) {
                ret = path;
            }
        }
        return ret;
    }

}
