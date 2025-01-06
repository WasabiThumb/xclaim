package io.github.wasabithumb.xclaim.integration;

import io.github.wasabithumb.xclaim.XClaim;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Integration {

    @ApiStatus.Internal
    static <T extends Integration> @Nullable T load(@NotNull Class<T> clazz, @NotNull XClaim runtime) {
        final IntegrationLoader<T> loader = new IntegrationLoader<>(clazz);
        T instance = loader.load(runtime.assets());
        if (instance == null) return null;
        loader.inject(instance, runtime);
        return instance;
    }

    //

    @ApiStatus.Internal
    default void onEnable() { }

    @ApiStatus.Internal
    default void onDisable() { }

    /**
     * The weight of this integration, used to decide which impl to use when multiple are available.
     */
    default int weight() {
        return 0;
    }

}
