package codes.wasabi.xclaim.map.impl.dynmap;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class DynmapMapService extends MapService {

    private final Plugin plugin;

    public DynmapMapService() throws MapServiceInitException {
        this.plugin = this.findPlugin("dynmap");
        Class<? extends Plugin> clazz = this.findClass("org.dynmap.bukkit.DynmapPlugin", Plugin.class);
        if (!clazz.isInstance(this.plugin)) {
            throw new MapServiceInitException("Dynmap plugin is class \"" + this.plugin.getClass().getName() + "\", not \"" + clazz.getName() + "\"");
        }
        boolean corePresent = false;
        try {
            Field f = clazz.getDeclaredField("core");
            try {
                f.setAccessible(true);
            } catch (Exception ignored) { }
            corePresent = (f.get(this.plugin) != null);
        } catch (Exception e) {
            XClaim.logger.log(Level.WARNING, XClaim.lang.get("dynmap-warn-core"));
        }
        if (!corePresent) throw new MapServiceInitException("Dynmap plugin core is null");
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        try {
            return (MapMarker) Class.forName("codes.wasabi.xclaim.map.impl.dynmap.DynmapMapMarker")
                    .getDeclaredMethod("getMarker", Plugin.class, Claim.class)
                    .invoke(null, this.plugin, claim);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            Class.forName("codes.wasabi.xclaim.map.impl.dynmap.DynmapMapMarker")
                    .getDeclaredMethod("cleanMarkerSet", Plugin.class)
                    .invoke(null, this.plugin);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
