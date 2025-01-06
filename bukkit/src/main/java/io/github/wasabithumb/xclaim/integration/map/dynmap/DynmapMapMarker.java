package io.github.wasabithumb.xclaim.integration.map.dynmap;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.util.BABB;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import io.github.wasabithumb.xclaim.util.hull.ConvexHull;
import io.github.wasabithumb.xclaim.util.tracer.ChunkBitmap;
import io.github.wasabithumb.xclaim.util.tracer.Point;
import org.dynmap.markers.AreaMarker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DynmapMapMarker implements MapMarker {

    private final AreaMarker handle;
    private final boolean oldStyle;
    public DynmapMapMarker(@NotNull AreaMarker handle, boolean oldStyle) {
        this.handle = handle;
        this.oldStyle = oldStyle;
    }

    //

    @Override
    public void update(@NotNull Claim claim) {
        List<Point> points = this.oldStyle ?
                this.calcPointsOld(claim) :
                this.calcPoints(claim);

        int count = points.size();
        double[] xs = new double[count];
        double[] zs = new double[count];
        Point point;
        for (int i=0; i < count; i++) {
            point = points.get(i);
            xs[i] = point.x();
            zs[i] = point.y();
        }

        this.handle.setCornerLocations(xs, zs);
    }

    @Override
    public void deleteMarker() {
        this.handle.deleteMarker();
    }

    //

    private @NotNull List<Point> calcPoints(@NotNull Claim claim) {
        ChunkBitmap bmp = new ChunkBitmap(claim.chunks());
        return bmp.traceBlocks();
    }

    private @NotNull List<Point> calcPointsOld(@NotNull Claim claim) {
        List<Point> points = new ArrayList<>();
        for (ChunkReference c : claim.chunks()) {
            BABB bounds = c.getBounds();
            points.add(new Point(bounds.minX(), bounds.minZ()));
            points.add(new Point(bounds.maxX(), bounds.minZ()));
            points.add(new Point(bounds.minX(), bounds.maxZ()));
            points.add(new Point(bounds.maxX(), bounds.maxZ()));
        }
        return ConvexHull.makeHull(points);
    }

}
