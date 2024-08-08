package codes.wasabi.xclaim.map.impl.bluemap;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.map.MapServiceOp;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class BluemapMapService extends MapService {

    private final Object tracker;
    private final Method trackerGet;
    private final Method trackerWith;

    public BluemapMapService() throws MapServiceInitException {
        Class<?> trackerClass = this.findClass("codes.wasabi.xclaim.map.impl.bluemap.BluemapAPITracker", Object.class);

        Object tracker;
        Method trackerGet;
        Method trackerWith;
        try {
            Constructor<?> con = trackerClass.getConstructor();
            tracker = con.newInstance();
            trackerGet = trackerClass.getMethod("get");
            trackerWith = trackerClass.getMethod("with", Consumer.class);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new MapServiceInitException("Failed to get instance of BlueMap API");
        }

        this.tracker = tracker;
        this.trackerGet = trackerGet;
        this.trackerWith = trackerWith;
    }

    private Object getInstanceNow() {
        Object instance;
        try {
            instance = this.trackerGet.invoke(this.tracker);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        Object instance = this.getInstanceNow();
        if (instance == null) return null;

        return this.getMarker(claim, instance);
    }

    private @Nullable MapMarker getMarker(@NotNull Claim claim, @NotNull Object instance) {
        try {
            return (MapMarker) Class.forName("codes.wasabi.xclaim.map.impl.bluemap.BluemapMapMarker")
                    .getDeclaredMethod("getMarker", Object.class, Claim.class)
                    .invoke(null, instance, claim);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void queueOperation(@NotNull MapServiceOp op) {
        Consumer<Object> action = o -> {
            MapMarker marker = BluemapMapService.this.getMarker(op.getClaim(), o);
            if (marker == null) return;
            op.apply(marker);
        };
        try {
            this.trackerWith.invoke(this.tracker, action);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void cleanup() {
        Object instance = this.getInstanceNow();
        if (instance == null) return;

        try {
            Class.forName("codes.wasabi.xclaim.map.impl.bluemap.BluemapMapMarker")
                    .getDeclaredMethod("cleanup", Object.class)
                    .invoke(null, instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
