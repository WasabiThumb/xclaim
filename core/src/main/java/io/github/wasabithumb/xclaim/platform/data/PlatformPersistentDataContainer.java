package io.github.wasabithumb.xclaim.platform.data;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformPersistentDataContainer extends PlatformObject {

    boolean has(@NotNull String key, @NotNull PlatformPersistentDataType<?> type);

    <T> @NotNull T get(@NotNull String key, @NotNull PlatformPersistentDataType<T> type);

    @Contract("_, _, !null -> !null; _, _, _ -> _")
    default <T> T getElse(@NotNull String key, @NotNull PlatformPersistentDataType<T> type, T fallback) {
        if (this.has(key, type)) return this.get(key, type);
        return fallback;
    }

    <T> void set(@NotNull String key, @NotNull PlatformPersistentDataType<T> type, @NotNull T value);

    void remove(@NotNull String key, @NotNull PlatformPersistentDataType<?> type);

}
