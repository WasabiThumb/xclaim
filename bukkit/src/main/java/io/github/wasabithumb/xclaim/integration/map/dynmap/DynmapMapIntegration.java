package io.github.wasabithumb.xclaim.integration.map.dynmap;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.MapConfig;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.map.MapIntegration;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.integration.map.MapOperation;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.util.ColorUtil;
import org.bukkit.World;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynmapMapIntegration implements MapIntegration {

    private static final String MARKER_SET_KEY = "xclaim_marker_set";
    private static final String MARKER_PREFIX = "claim_marker_";

    //

    private final DynmapAPITracker api;

    @IntegrationInject
    private Lang lang;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    @IntegrationInject
    private MapConfig config;

    public DynmapMapIntegration() {
        this.api = new DynmapAPITracker();
    }

    //

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        DynmapCommonAPI api = this.api.get();
        if (api == null) return null;
        return this.getMarker(claim, api);
    }

    @Override
    public void queueOperation(@NotNull MapOperation op) {
        this.api.with((DynmapCommonAPI api) -> {
            MapMarker marker = this.getMarker(op.getClaim(), api);
            if (marker == null) return;
            op.apply(marker);
        });
    }

    @Override
    public void onDisable() {
        DynmapCommonAPI api = this.api.get();
        if (api != null) {
            this.getMarkerSet(api)
                    .deleteMarkerSet();
        }
        this.api.cleanup();
    }

    //

    private @NotNull MarkerSet getMarkerSet(@NotNull DynmapCommonAPI api) {
        MarkerAPI markerAPI = api.getMarkerAPI();
        MarkerSet ms = markerAPI.getMarkerSet(MARKER_SET_KEY);
        if (ms == null) {
            ms = markerAPI.createMarkerSet(
                    MARKER_SET_KEY,
                    this.lang.get("dynmap-marker-name"),
                    null,
                    false
            );
        }
        return ms;
    }

    private @Nullable MapMarker getMarker(@NotNull Claim claim, @NotNull DynmapCommonAPI api) {
        World w = (World) this.adapter.world(claim.world());
        if (w == null) return null;

        MarkerSet ms = this.getMarkerSet(api);
        String id = MARKER_PREFIX + claim.token();

        AreaMarker marker = ms.findAreaMarker(id);
        if (marker == null) {
            marker = ms.createAreaMarker(
                    id,
                    claim.name(),
                    false,
                    w.getName(),
                    new double[] { 0, 0 },
                    new double[] { 0, 0 },
                    false
            );
            int color = ColorUtil.uuidToColor(claim.owner().uuid()).getRGB();
            marker.setFillStyle(0.4d, color);
            marker.setLineStyle(3, 0.6d, color);
        }

        return new DynmapMapMarker(marker, this.config.oldOutlineStyle());
    }

}
