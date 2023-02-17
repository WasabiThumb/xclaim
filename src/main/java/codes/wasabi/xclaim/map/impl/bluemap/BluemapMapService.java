package codes.wasabi.xclaim.map.impl.bluemap;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.map.exception.MapServiceInitException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class BluemapMapService extends MapService {

    private final CompletableFuture<Object> apiInstanceFuture = new CompletableFuture<>();

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
            Object apiInstance = optional.get();
            if (!apiClass.isInstance(apiInstance)) {
                throw new MapServiceInitException(
                        "BlueMap API gave an instance of class " +
                        "\"" + apiInstance.getClass().getName() + "\"" +
                        "that is not a subclass of \"" + apiClass.getName() + "\""
                );
            }
            this.apiInstanceFuture.complete(apiInstance);
        } else {
            try {
                de.bluecolored.bluemap.api.BlueMapAPI.onEnable(this.apiInstanceFuture::complete);
            } catch (Exception e) {
                throw new MapServiceInitException("Uncategorized exception while attaching enable listener to BlueMap API");
            }
        }
    }

    @Override
    public @Nullable MapMarker getMarker(@NotNull Claim claim) {
        try {
            return this.getMarker(claim, this.apiInstanceFuture.get());
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void getMarkerAsync(@NotNull Claim claim, @NotNull Consumer<@Nullable MapMarker> callback) {
        if (this.apiInstanceFuture.isDone()) {
            callback.accept(this.getMarker(claim));
            return;
        }
        this.apiInstanceFuture.whenCompleteAsync((Object instance, Throwable t) -> {
            if (instance != null) callback.accept(this.getMarker(claim, instance));
        });
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
    public void cleanup() {
        Object instance;
        try {
            instance = this.apiInstanceFuture.getNow(null);
            if (instance == null) return;
        } catch (Exception ignored) {
            return;
        }

        try {
            Class.forName("codes.wasabi.xclaim.map.impl.bluemap.BluemapMapMarker")
                    .getDeclaredMethod("cleanup", Object.class)
                    .invoke(null, instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
