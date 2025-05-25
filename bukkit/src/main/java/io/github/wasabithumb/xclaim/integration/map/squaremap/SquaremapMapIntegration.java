package io.github.wasabithumb.xclaim.integration.map.squaremap;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.map.MapIntegration;
import io.github.wasabithumb.xclaim.integration.map.MapMarker;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Polygon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SquaremapMapIntegration implements MapIntegration {

    private final Squaremap api;
    private final Key layerKey;

    @IntegrationInject
    private Lang lang;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    //

    public SquaremapMapIntegration() {
        Squaremap api = Bukkit.getServicesManager().load(Squaremap.class);
        if (api == null) throw new IntegrationException("No squaremap registration");
        this.api = api;
        this.layerKey = Key.of("xclaim_layer");
    }

    //

    @Override
    public int weight() {
        return 2;
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        World w = (World) this.adapter.world(claim.world());
        if (w == null) return null;

        WorldIdentifier wid = BukkitAdapter.worldIdentifier(w);
        Optional<MapWorld> opt = this.api.getWorldIfEnabled(wid);
        if (opt.isEmpty()) return null;

        MapWorld mw = opt.get();
        SimpleLayerProvider layer = this.getLayer(mw);
        Key markerKey = this.getMarkerKey(claim);

        Marker marker = layer.registeredMarkers().get(markerKey);
        Polygon polygonMarker;
        if (marker instanceof Polygon) {
            polygonMarker = (Polygon) marker;
        } else {
            if (marker != null) layer.removeMarker(markerKey);
            PlatformUser owner = claim.owner();
            Color color = ColorUtil.uuidToColor(owner.uuid());

            String ownerName;
            if (owner.isPlayer()) {
                ownerName = owner.asPlayer().name();
            } else {
                ownerName = owner.displayName();
            }

            List<Point> points = new ArrayList<>();
            List<List<Point>> negatives = new ArrayList<>();
            SquaremapMapMarker.populateLists(claim, points, negatives);

            polygonMarker = Polygon.polygon(points, negatives);
            polygonMarker.markerOptions(MarkerOptions.builder()
                    .hoverTooltip(claim.name())
                    .clickTooltip(ownerName)
                    .fillColor(color)
                    .fillOpacity(0.2)
                    .strokeColor(color)
                    .strokeOpacity(0.4)
            );
            layer.addMarker(markerKey, polygonMarker);
        }

        return new SquaremapMapMarker(layer, markerKey, polygonMarker);
    }

    @Override
    public void onDisable() {
        for (MapWorld mw : this.api.mapWorlds()) {
            Registry<LayerProvider> layerRegistry = mw.layerRegistry();
            if (!layerRegistry.hasEntry(this.layerKey)) continue;
            layerRegistry.unregister(this.layerKey);
        }
    }

    //

    private @NotNull SimpleLayerProvider getLayer(@NotNull MapWorld mw) {
        Registry<LayerProvider> layerRegistry = mw.layerRegistry();
        SimpleLayerProvider layer;
        if (layerRegistry.hasEntry(this.layerKey)) {
            layer = (SimpleLayerProvider) layerRegistry.get(this.layerKey);
        } else {
            layer = SimpleLayerProvider.builder(this.lang.get(I18N.DYNMAP_MARKER_NAME))
                    .defaultHidden(true)
                    .build();
            layerRegistry.register(this.layerKey, layer);
        }
        return layer;
    }

    private @NotNull Key getMarkerKey(@NotNull Claim claim) {
        return Key.of("xclaim_claim_" + claim.token());
    }

}
