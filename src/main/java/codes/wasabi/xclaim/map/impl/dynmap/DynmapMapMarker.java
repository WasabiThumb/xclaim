package codes.wasabi.xclaim.map.impl.dynmap;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.util.ChunkBitmap;
import codes.wasabi.xclaim.map.util.ClaimUtil;
import codes.wasabi.xclaim.map.util.Point;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.hull.ConvexHull;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DynmapMapMarker implements MapMarker {

    private final AreaMarker marker;
    public DynmapMapMarker(AreaMarker marker) {
        this.marker = marker;
    }

    public AreaMarker get() {
        return marker;
    }

    @Override
    public void update(@NotNull Claim claim) {
        int minHeight = 0;
        World w = claim.getWorld();
        if (w != null) minHeight = Platform.get().getWorldMinHeight(w);
        List<Point> points;
        if (XClaim.mainConfig.getBoolean("dynmap-integration.use-old-outline-style", false)) {
            points = new ArrayList<>();
            for (Chunk c : claim.getChunks()) {
                Block cornerBlock = c.getBlock(0, minHeight, 0);
                int cornerX = cornerBlock.getX();
                int cornerZ = cornerBlock.getZ();
                points.add(new Point(cornerX, cornerZ));
                points.add(new Point(cornerX + 16, cornerZ));
                points.add(new Point(cornerX, cornerZ + 16));
                points.add(new Point(cornerX + 16, cornerZ + 16));
            }
            points = ConvexHull.makeHull(points);
        } else {
            ChunkBitmap bmp = new ChunkBitmap(claim.getChunks());
            points = bmp.traceBlocks();
        }
        double[] xLocations = new double[points.size()];
        double[] zLocations = new double[points.size()];
        for (int i=0; i < points.size(); i++) {
            Point point = points.get(i);
            xLocations[i] = point.x();
            zLocations[i] = point.y();
        }
        this.marker.setCornerLocations(xLocations, zLocations);
    }

    @Override
    public void deleteMarker() {
        this.marker.deleteMarker();
    }

    private static @NotNull MarkerSet getMarkerSet(@NotNull DynmapPlugin dynmap) {
        MarkerAPI api = dynmap.getMarkerAPI();
        MarkerSet ms = api.getMarkerSet("xclaim_marker_set");
        if (ms == null) {
            ms = api.createMarkerSet("xclaim_marker_set", XClaim.lang.get("dynmap-marker-name"), null, false);
        }
        return ms;
    }

    // Package Private
    static @Nullable DynmapMapMarker getMarker(@NotNull Plugin plugin, @NotNull Claim claim) {
        DynmapPlugin dynmap = (DynmapPlugin) plugin;
        World w = claim.getWorld();
        if (w == null) return null;
        MarkerSet set = getMarkerSet(dynmap);
        String identifier = "claim_marker_" + claim.getUniqueToken();
        AreaMarker marker = set.findAreaMarker(identifier);
        if (marker == null) {
            marker = set.createAreaMarker(identifier, claim.getName(), false, w.getName(), new double[]{ 0, 0 }, new double[]{ 0, 0 }, false);
            AreaMarker finalMarker = marker;
            Consumer<Claim> updateColor = ((Claim c) -> {
                Color color = ClaimUtil.getClaimColor(c);
                int rgb = (color.getRed() << 16)
                        | (color.getGreen() << 8)
                        | color.getBlue();
                finalMarker.setFillStyle(0.4d, rgb);
                finalMarker.setLineStyle(3, 0.6d, rgb);
            });
            updateColor.accept(claim);
            claim.onOwnerChanged(updateColor);
        }
        return new DynmapMapMarker(marker);
    }

    static void cleanMarkerSet(@NotNull Plugin plugin) {
        DynmapPlugin dynmap = (DynmapPlugin) plugin;
        MarkerSet ms = getMarkerSet(dynmap);
        ms.deleteMarkerSet();
    }

}
