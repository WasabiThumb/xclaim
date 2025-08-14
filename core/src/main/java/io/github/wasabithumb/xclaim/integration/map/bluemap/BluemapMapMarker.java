package io.github.wasabithumb.xclaim.integration.map.bluemap;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Shape;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.util.tracer.ChunkBitmap;
import io.github.wasabithumb.xclaim.util.tracer.Point;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class BluemapMapMarker implements MapMarker {

    private final MarkerSet set;
    private final ExtrudeMarker marker;

    public BluemapMapMarker(
            @NotNull MarkerSet set,
            @NotNull ExtrudeMarker marker
    ) {
        this.set = set;
        this.marker = marker;
    }

    //

    @Override
    public void update(@NotNull Claim claim) {
        ChunkBitmap bmp = new ChunkBitmap(claim.chunks());
        List<List<Point>> edges = bmp.traceBlocks(true);

        PlatformWorld world = claim.world();
        int minHeight;
        int maxHeight;
        if (world != null) {
            minHeight = world.getMinHeight();
            maxHeight = world.getMaxHeight();
        } else {
            minHeight = 0;
            maxHeight = 256;
        }

        this.marker.setShape(
                this.buildShapeFromPoints(edges.getFirst()),
                minHeight,
                maxHeight
        );

        Collection<Shape> holes = this.marker.getHoles();
        holes.clear();
        for (int i=1; i < edges.size(); i++) {
            holes.add(this.buildShapeFromPoints(edges.get(i)));
        }
    }

    @Override
    public void deleteMarker() {
        final List<Map.Entry<String, Marker>> entries = List.copyOf(
                this.set.getMarkers().entrySet()
        );
        final int count = entries.size();
        final BitSet marked = new BitSet(count);

        for (int i=0; i < count; i++) {
            if (this.marker.equals(entries.get(i).getValue())) {
                marked.set(i);
            }
        }

        int index = 0;
        while ((index = marked.nextSetBit(index)) != -1) {
            this.set.remove(entries.get(index).getKey());
            index++;
        }
    }

    //

    private @NotNull Shape buildShapeFromPoints(@NotNull List<Point> points) {
        Shape.Builder shapeBuilder = Shape.builder();
        for (Point p : points) {
            shapeBuilder.addPoint(Vector2d.from(p.x(), p.y()));
        }
        return shapeBuilder.build();
    }

}
