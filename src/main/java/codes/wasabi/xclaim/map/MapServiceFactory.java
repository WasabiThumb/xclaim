package codes.wasabi.xclaim.map;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import codes.wasabi.xclaim.map.impl.bluemap.BluemapMapService;
import codes.wasabi.xclaim.map.impl.dynmap.DynmapMapService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public final class MapServiceFactory {

    private static final List<Class<? extends MapService>> services = Arrays.asList(
            DynmapMapService.class,
            BluemapMapService.class
    );

    public static @NotNull MapService create() throws MapServiceInitException {
        MapServiceInitException e = null;
        for (Class<? extends MapService> clazz : services) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InvocationTargetException e1) {
                Throwable cause = e1.getCause();
                if (cause instanceof MapServiceInitException) {
                    e = (MapServiceInitException) cause;
                } else {
                    e1.printStackTrace();
                    throw new MapServiceInitException("Unexpected error in initializer for \"" + clazz.getName() + "\"");
                }
            } catch (ReflectiveOperationException | SecurityException | LinkageError e1) {
                e1.printStackTrace();
                throw new MapServiceInitException("Unexpected error in initializer for \"" + clazz.getName() + "\"");
            }
        }
        if (e != null) throw e;
        throw new MapServiceInitException("No map services found to load");
    }

    public static @Nullable MapService createElseNull() {
        try {
            return create();
        } catch (MapServiceInitException e) {
            if (XClaim.mainConfig.getBoolean("dynmap-integration.debug", false)) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
