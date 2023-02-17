package codes.wasabi.xclaim.map.impl.bluemap;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.util.ChunkBitmap;
import codes.wasabi.xclaim.map.util.ClaimUtil;
import codes.wasabi.xclaim.map.util.Point;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.BoundingBox;
import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BluemapMapMarker implements MapMarker {

    private final MarkerSet set;
    private final ExtrudeMarker marker;

    public BluemapMapMarker(MarkerSet set, ExtrudeMarker marker) {
        this.set = set;
        this.marker = marker;
    }

    public ExtrudeMarker get() {
        return marker;
    }

    @Override
    public void update(@NotNull Claim claim) {
        ChunkBitmap bmp = new ChunkBitmap(claim.getChunks());
        Shape.Builder shapeBuilder = Shape.builder();

        for (Point p : bmp.traceBlocks()) {
            shapeBuilder.addPoint(Vector2d.from(p.x(), p.y()));
        }

        Shape shape = shapeBuilder.build();
        this.marker.setShape(
                shape,
                this.marker.getShapeMinY(),
                this.marker.getShapeMaxY()
        );
    }

    @Override
    public void deleteMarker() {
        (new HashSet<>(set.getMarkers().entrySet())).stream()
                .filter((Map.Entry<String, Marker> entry) -> Objects.equals(entry.getValue(), this.marker))
                .map(Map.Entry::getKey)
                .forEach(set::remove);
    }

    // Package Private
    private static final String markerSetId = "xclaim_marker_set";
    private static final Map<UUID, MarkerSet> markerSetMap = new HashMap<>();
    private static @Nullable MarkerSet getMarkerSet(BlueMapAPI api, Claim claim) {
        World world = claim.getWorld();
        if (world == null) return null;
        UUID uuid = world.getUID();
        if (markerSetMap.containsKey(uuid)) return markerSetMap.get(uuid);

        Optional<BlueMapWorld> opt = api.getWorld(world);
        if (!opt.isPresent()) return null;
        BlueMapWorld bmw = opt.get();

        MarkerSet ms = MarkerSet.builder()
                .label(XClaim.lang.get("dynmap-marker-name"))
                .build();
        markerSetMap.put(uuid, ms);

        for (BlueMapMap map : bmw.getMaps()) {
            map.getMarkerSets().put(markerSetId, ms);
        }
        return ms;
    }

    static @Nullable BluemapMapMarker getMarker(Object apiInstance, Claim claim) {
        BlueMapAPI api = (BlueMapAPI) apiInstance;
        MarkerSet ms = getMarkerSet(api, claim);
        if (ms == null) return null;

        String token = claim.getUniqueToken();
        Marker existing = ms.get(token);
        if (existing != null) {
            if (existing instanceof ExtrudeMarker) {
                return new BluemapMapMarker(ms, (ExtrudeMarker) existing);
            } else {
                ms.remove(token);
            }
        }

        BoundingBox bounds = claim.getOuterBounds();
        Vector mins = bounds.getMins();
        Vector maxs = bounds.getMaxs();

        World world = claim.getWorld();
        if (world == null) {
            world = Bukkit.getWorlds().stream().findFirst().orElseThrow(null);
        }

        java.awt.Color col = ClaimUtil.getClaimColor(claim);

        ExtrudeMarker marker = ExtrudeMarker.builder()
                .shape(
                        Shape.createRect(
                                mins.getX(), mins.getZ(),
                                maxs.getX(), maxs.getZ()
                        ),
                        Platform.get().getWorldMinHeight(world),
                        world.getMaxHeight()
                )
                .label(claim.getName())
                .fillColor(new Color(
                        col.getRed(),
                        col.getGreen(),
                        col.getBlue()
                ))
                .build();

        ms.put(token, marker);

        return new BluemapMapMarker(ms, marker);
    }

    static void cleanup(Object apiInstance) {
        BlueMapAPI api = (BlueMapAPI) apiInstance;
        for (BlueMapMap map : api.getMaps()) {
            map.getMarkerSets().remove(markerSetId);
        }
    }

}
