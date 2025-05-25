package io.github.wasabithumb.xclaim.integration.map.squaremap;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import io.github.wasabithumb.xclaim.util.tracer.ChunkBitmap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.marker.Polygon;

import java.util.ArrayList;
import java.util.List;

record SquaremapMapMarker(
        @NotNull SimpleLayerProvider layer,
        @NotNull Key key,
        @NotNull Polygon handle
) implements MapMarker {

    @Contract(mutates = "param2, param3")
    static void populateLists(@NotNull Claim claim, @NotNull List<Point> points, @NotNull List<List<Point>> negatives) {
        ChunkBitmap bmp = new ChunkBitmap(claim.chunks());
        List<List<io.github.wasabithumb.xclaim.util.tracer.Point>> src = bmp.traceBlocks(true);
        int len = src.size();
        assert len != 0;
        points.addAll(new ProxyList<>(src.getFirst(), SquaremapMapMarker::adaptPoint));
        for (int i=1; i < len; i++) {
            List<io.github.wasabithumb.xclaim.util.tracer.Point> subSrc = src.get(i);
            List<Point> sub = new ArrayList<>(subSrc.size());
            for (io.github.wasabithumb.xclaim.util.tracer.Point p : subSrc) sub.add(adaptPoint(p));
            negatives.add(sub);
        }
    }

    private static @NotNull Point adaptPoint(@NotNull io.github.wasabithumb.xclaim.util.tracer.Point src) {
        return Point.of(src.x(), src.y());
    }

    //

    @Override
    public void update(@NotNull Claim claim) {
        List<Point> points = this.handle.mainPolygon();
        List<List<Point>> negatives = this.handle.negativeSpace();
        points.clear();
        negatives.clear();
        populateLists(claim, points, negatives);
    }

    @Override
    public void deleteMarker() {
        this.layer.removeMarker(this.key);
    }

}
