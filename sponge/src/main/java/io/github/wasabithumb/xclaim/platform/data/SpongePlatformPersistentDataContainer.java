package io.github.wasabithumb.xclaim.platform.data;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.type.PlatformPersistentDataType;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;

public class SpongePlatformPersistentDataContainer implements PlatformPersistentDataContainer {

    protected final SpongePlatform platform;
    protected final DataHolder.Mutable handle;

    public SpongePlatformPersistentDataContainer(@NotNull SpongePlatform platform, @NotNull DataHolder.Mutable handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull DataHolder.Mutable handle() {
        return this.handle;
    }

    //

    @Override
    public boolean has(@NotNull String key, @NotNull PlatformPersistentDataType<?> type) {
        return this.handle.get(this.createKey(key, type)).isPresent();
    }

    @Override
    public <T> @NotNull T get(@NotNull String key, @NotNull PlatformPersistentDataType<T> type) {
        return this.handle.getOrElse(this.createKey(key, type), type.fallback());
    }

    @Override
    public <T> void set(@NotNull String key, @NotNull PlatformPersistentDataType<T> type, @NotNull T value) {
        if (!this.handle.offer(this.createKey(key, type), value).isSuccessful()) {
            // TODO: This tends to happen a lot
            this.platform.logger().log(
                    Level.WARN,
                    "DataHolder rejected data of type {}",
                    type.valueClass().getName()
            );
        }
    }

    @Override
    public void remove(@NotNull String key, @NotNull PlatformPersistentDataType<?> type) {
        this.handle.remove(this.createKey(key, type));
    }

    //

    protected <V> @NotNull Key<Value<V>> createKey(@NotNull String key, @NotNull PlatformPersistentDataType<V> type) {
        return Key.builder()
                .elementType(type.valueClass())
                .key(ResourceKey.of(this.platform.plugin(), key))
                .build();
    }

}
