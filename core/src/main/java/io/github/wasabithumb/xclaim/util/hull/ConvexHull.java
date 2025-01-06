package io.github.wasabithumb.xclaim.util.hull;

import io.github.wasabithumb.xclaim.util.tracer.Point;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Obsolete
public final class ConvexHull {

    public static @NotNull List<Point> makeHull(@NotNull List<Point> points) {
        List<Point> newPoints = new ArrayList<>(points);
        Collections.sort(newPoints);
        return makeHullPresorted(newPoints);
    }

    public static @NotNull List<Point> makeHullPresorted(@NotNull List<Point> points) {
        if (points.size() <= 1) return new ArrayList<>(points);
        List<Point> upperHull = makeUpperHull(points);
        List<Point> lowerHull = makeLowerHull(points);
        if (!(upperHull.size() == 1 && upperHull.equals(lowerHull)))
            upperHull.addAll(lowerHull);
        return upperHull;
    }

    private static @NotNull List<Point> makeUpperHull(@NotNull List<Point> points) {
        List<Point> upperHull = new ArrayList<>();
        for (Point p : points) {
            while (upperHull.size() >= 2) {
                Point q = upperHull.getLast();
                Point r = upperHull.get(upperHull.size() - 2);
                if ((q.x() - r.x()) * (p.y() - r.y()) >= (q.y() - r.y()) * (p.x() - r.x()))
                    upperHull.removeLast();
                else
                    break;
            }
            upperHull.add(p);
        }
        upperHull.removeLast();
        return upperHull;
    }

    private static @NotNull List<Point> makeLowerHull(@NotNull List<Point> points) {
        List<Point> lowerHull = new ArrayList<>();
        for (int i = points.size() - 1; i >= 0; i--) {
            Point p = points.get(i);
            while (lowerHull.size() >= 2) {
                Point q = lowerHull.getLast();
                Point r = lowerHull.get(lowerHull.size() - 2);
                if ((q.x() - r.x()) * (p.y() - r.y()) >= (q.y() - r.y()) * (p.x() - r.x()))
                    lowerHull.removeLast();
                else
                    break;
            }
            lowerHull.add(p);
        }
        lowerHull.removeLast();
        return lowerHull;
    }

}
