package codes.wasabi.xclaim.map.impl.bluemap;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;

public class BluemapMapService extends MapService {

    private final Object apiInstance;

    public BluemapMapService() throws MapServiceInitException {
        Class<?> apiClass = this.findClass("de.bluecolored.bluemap.api.BlueMapAPI", Object.class);
        Object optionalObject;
        try {
            Method m = apiClass.getDeclaredMethod("getInstance");
            optionalObject = m.invoke(null);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new MapServiceInitException("Failed to get instance of BlueMap API");
        }
        Optional<?> optional;
        try {
            optional = (Optional<?>) optionalObject;
        } catch (ClassCastException e) {
            throw new MapServiceInitException("BlueMap API gave an unexpected value");
        }
        if (optional.isPresent()) {
            this.apiInstance = optional.get();
            if (!apiClass.isInstance(this.apiInstance)) {
                throw new MapServiceInitException(
                        "BlueMap API gave an instance of class " +
                        "\"" + this.apiInstance.getClass().getName() + "\"" +
                        "that is not a subclass of \"" + apiClass.getName() + "\""
                );
            }
        } else {
            throw new MapServiceInitException("BlueMap API is not enabled");
        }
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        try {
            return (MapMarker) Class.forName("codes.wasabi.xclaim.map.impl.bluemap.BluemapMapMarker")
                    .getDeclaredMethod("getMarker", Object.class, Claim.class)
                    .invoke(null, this.apiInstance, claim);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            Class.forName("codes.wasabi.xclaim.map.impl.bluemap.BluemapMapMarker")
                    .getDeclaredMethod("cleanup", Object.class)
                    .invoke(null, this.apiInstance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
