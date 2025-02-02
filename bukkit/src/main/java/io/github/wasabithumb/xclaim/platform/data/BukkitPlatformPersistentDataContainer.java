package io.github.wasabithumb.xclaim.platform.data;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.type.PlatformPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BukkitPlatformPersistentDataContainer implements PlatformPersistentDataContainer {

    private final BukkitPlatform platform;
    private final PersistentDataContainer handle;

    @ApiStatus.Internal
    public BukkitPlatformPersistentDataContainer(@NotNull BukkitPlatform platform, @NotNull PersistentDataContainer handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull PersistentDataContainer handle() {
        return this.handle;
    }

    @Override
    public boolean has(@NotNull String key, @NotNull PlatformPersistentDataType<?> type) {
        return this.handle.has(this.namespaced(key), this.adapt(type));
    }

    @Override
    public <T> @NotNull T get(@NotNull String key, @NotNull PlatformPersistentDataType<T> type) {
        Object value = this.handle.get(this.namespaced(key), this.adapt(type));
        if (value == null) return type.fallback();
        return type.valueClass().cast(value);
    }

    @Override
    public <T> void set(@NotNull String key, @NotNull PlatformPersistentDataType<T> type, @NotNull T value) {
        this.setInternal(this.namespaced(key), this.adapt(type), value);
    }

    private <Q> void setInternal(@NotNull NamespacedKey key, @NotNull PersistentDataType<?, Q> type, @NotNull Object value) {
        this.handle.set(key, type, type.getComplexType().cast(value));
    }

    @Override
    public void remove(@NotNull String key, @NotNull PlatformPersistentDataType<?> ignored) {
        this.handle.remove(this.namespaced(key));
    }

    //

    @Contract("_ -> new")
    private @NotNull NamespacedKey namespaced(@NotNull String key) {
        return new NamespacedKey(this.platform.plugin(), key);
    }

    private <T> @NotNull PersistentDataType<?, ?> adapt(@NotNull PlatformPersistentDataType<T> type) {
        return switch (type.ordinal()) {
            case 0 -> PersistentDataType.STRING;
            case 1 -> PersistentDataType.BYTE;
            case 2 -> PersistentDataType.BYTE_ARRAY;
            case 3 -> PersistentDataType.LONG;
            default -> throw new AssertionError("Unhandled type ordinal: " + type.ordinal());
        };
    }

}
