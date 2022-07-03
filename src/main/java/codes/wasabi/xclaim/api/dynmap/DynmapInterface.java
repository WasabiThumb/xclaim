package codes.wasabi.xclaim.api.dynmap;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.dynmap.outline.ChunkBitmap;
import codes.wasabi.xclaim.api.dynmap.outline.Point;
import codes.wasabi.xclaim.util.ColorUtil;
import codes.wasabi.xclaim.util.hull.ConvexHull;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.markers.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class DynmapInterface {

    private final DynmapPlugin dynmap;

    public DynmapInterface(DynmapPlugin plugin) {
        dynmap = plugin;
        XClaim.logger.log(Level.INFO, XClaim.lang.get("dynmap-hooked", dynmap.getDynmapVersion()));
    }

    public String getVersion() {
        return dynmap.getDynmapVersion();
    }

    private final Map<UUID, Color> colorMap = new HashMap<>();
    public @NotNull Color getClaimColor(@NotNull Claim claim) {
        XCPlayer ply = claim.getOwner();
        UUID uuid = ply.getUniqueId();
        Color color = colorMap.get(uuid);
        if (color == null) {
            color = ColorUtil.uuidToColor(uuid);
            colorMap.put(uuid, color);
        }
        return color;
    }

    private @NotNull String getClaimIdentifier(@NotNull Claim claim) {
        return new String(Base64.getEncoder().encode(claim.getName().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    private @NotNull MarkerSet getMarkerSet() {
        MarkerAPI api = dynmap.getMarkerAPI();
        MarkerSet ms = api.getMarkerSet("claim_marker_set");
        if (ms == null) {
            ms = api.createMarkerSet("claim_marker_set", XClaim.lang.get("dynmap-marker-name"), null, false);
        }
        return ms;
    }

    public @Nullable AreaMarker getMarker(@NotNull Claim claim) {
        World w = claim.getWorld();
        if (w == null) return null;
        MarkerSet set = getMarkerSet();
        String identifier = "claim_marker_" + getClaimIdentifier(claim);
        AreaMarker marker = set.findAreaMarker(identifier);
        if (marker == null) {
            marker = set.createAreaMarker(identifier, claim.getName(), false, w.getName(), new double[]{ 0, 0 }, new double[]{ 0, 0 }, false);
            AreaMarker finalMarker = marker;
            Consumer<Claim> updateColor = ((Claim c) -> {
                Color color = getClaimColor(c);
                int rgb = (color.getRed() << 16)
                        | (color.getGreen() << 8)
                        | color.getBlue();
                finalMarker.setFillStyle(0.4d, rgb);
                finalMarker.setLineStyle(3, 0.6d, rgb);
            });
            updateColor.accept(claim);
            claim.onOwnerChanged(updateColor);
        }
        return marker;
    }

    public void updateMarker(@NotNull AreaMarker marker, @NotNull Claim claim) {
        int minHeight = 0;
        World w = claim.getWorld();
        if (w != null) minHeight = w.getMinHeight();
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
        marker.setCornerLocations(xLocations, zLocations);
    }

    public void cleanup() {
        MarkerSet ms = getMarkerSet();
        ms.deleteMarkerSet();
    }

}
