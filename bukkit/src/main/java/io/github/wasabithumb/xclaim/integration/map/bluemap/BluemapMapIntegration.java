package io.github.wasabithumb.xclaim.integration.map.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.map.MapIntegration;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.integration.map.MapOperation;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.util.ColorUtil;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BluemapMapIntegration implements MapIntegration {

    private static final String MARKER_SET_ID = "xclaim_marker_set";

    //

    private final BluemapAPITracker api;
    private final Map<UUID, MarkerSet> markerSets;

    @IntegrationInject
    private Lang lang;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    public BluemapMapIntegration() {
        this.api = new BluemapAPITracker();
        this.markerSets = Collections.synchronizedMap(new HashMap<>());
    }

    //

    @Override
    public int weight() {
        return 1;
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        BlueMapAPI api = this.api.get();
        if (api == null) return null;
        return this.getMarker(claim, api);
    }

    @Override
    public void queueOperation(@NotNull MapOperation op) {
        this.api.with((BlueMapAPI api) -> {
            MapMarker marker = this.getMarker(op.getClaim(), api);
            if (marker == null) return;
            op.apply(marker);
        });
    }

    @Override
    public void onDisable() {
        BlueMapAPI api = this.api.get();
        if (api == null) return;
        for (BlueMapMap map : api.getMaps()) {
            map.getMarkerSets().remove(MARKER_SET_ID);
        }
        this.api.cleanup();
    }

    //

    private @Nullable MapMarker getMarker(@NotNull Claim claim, @NotNull BlueMapAPI api) {
        MarkerSet ms = this.getMarkerSet(claim, api);
        if (ms == null) return null;

        String token = claim.token();
        Marker existing = ms.get(token);
        if (existing != null) {
            if (existing instanceof ExtrudeMarker extrude) {
                return new BluemapMapMarker(ms, extrude);
            } else {
                ms.remove(token);
            }
        }

        int color = ColorUtil.uuidToColor(claim.owner().uuid()).getRGB();
        ExtrudeMarker marker = ExtrudeMarker.builder()
                .shape(Shape.createRect(0d, 0d, 0d, 0d), 0f, 0f)
                .label(claim.name())
                .fillColor(new Color(color, 0.2f))
                .lineColor(new Color(color, 0.4f))
                .build();

        ms.put(token, marker);
        return new BluemapMapMarker(ms, marker);
    }

    private @Nullable MarkerSet getMarkerSet(@NotNull Claim claim, @NotNull BlueMapAPI api) {
        World world = (World) this.adapter.world(claim.world());
        if (world == null) return null;
        UUID uid = world.getUID();

        MarkerSet ret;
        BlueMapWorld bmw;
        synchronized (this.markerSets) {
            ret = this.markerSets.get(uid);
            if (ret != null) return ret;

            Optional<BlueMapWorld> opt = api.getWorld(world);
            if (opt.isEmpty()) return null;
            bmw = opt.get();

            ret = MarkerSet.builder()
                    .label(this.lang.get("dynmap-marker-name"))
                    .build();
            this.markerSets.put(uid, ret);
        }

        for (BlueMapMap map : bmw.getMaps()) {
            map.getMarkerSets().put(MARKER_SET_ID, ret);
        }
        return ret;
    }

}
