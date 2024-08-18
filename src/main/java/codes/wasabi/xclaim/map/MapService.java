package codes.wasabi.xclaim.map;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import codes.wasabi.xclaim.map.impl.bluemap.BluemapMapService;
import codes.wasabi.xclaim.map.impl.dynmap.DynmapMapService;
import codes.wasabi.xclaim.util.service.ServiceFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class MapService {

    private static MapService instance = null;
    private static boolean instanceInit = false;
    private static boolean instanceValid = false;

    public static @Nullable MapService get() {
        if (instanceInit) {
            return instance;
        }
        if (!XClaim.mainConfig.getBoolean("dynmap-integration.enabled", true)) {
            instanceValid = false;
            instanceInit = true;
            return null;
        }
        ServiceFactory<MapService> factory = new ServiceFactory<>(
                BluemapMapService.class,
                DynmapMapService.class
        );
        instance = factory.createElseNull(XClaim.mainConfig.getBoolean("dynmap-integration.debug", false));
        instanceValid = instance != null;
        instanceInit = true;
        return instance;
    }

    public static @NotNull MapService getNonNull() {
        if (!instanceInit) get();
        return Objects.requireNonNull(instance);
    }

    public static boolean isAvailable() {
        if (!instanceInit) get();
        return instanceValid;
    }

    public static void unload() {
        MapService oldInstance = instance;
        boolean oldValid = instanceValid;
        instanceValid = false;
        instance = null;
        instanceInit = true;
        if (oldValid) oldInstance.cleanup();
    }

    protected MapService() throws MapServiceInitException {
    }

    public abstract @Nullable MapMarker getMarker(@NotNull Claim claim);

    public void queueOperation(@NotNull MapServiceOp op) {
        MapMarker marker = this.getMarker(op.getClaim());
        if (marker != null) op.apply(marker);
    }

    public abstract void cleanup();

    // Init Helpers
    protected final @NotNull Plugin findPlugin(String name) throws MapServiceInitException {
        Plugin plug = Bukkit.getPluginManager().getPlugin(name);
        if (plug == null) throw new MapServiceInitException("Failed to find plugin \"" + name + "\"");
        if (!plug.isEnabled()) throw new MapServiceInitException("Plugin \"" + name + "\" is not enabled");
        return plug;
    }

    protected final <T> @NotNull Class<? extends T> findClass(String name, Class<T> superclass) throws MapServiceInitException {
        try {
            Class<?> ret = Class.forName(name);
            if (superclass.isAssignableFrom(ret)) {
                return ret.asSubclass(superclass);
            } else {
                throw new MapServiceInitException("Class \"" + name + "\" is not a subclass of \"" + superclass.getName() + "\"");
            }
        } catch (ReflectiveOperationException e) {
            throw new MapServiceInitException("Failed to find class \"" + name + "\"");
        } catch (LinkageError e) {
            e.printStackTrace();
            throw new MapServiceInitException("Unexpected linkage error occurred while finding class \"" + name + "\"");
        }
    }

}
