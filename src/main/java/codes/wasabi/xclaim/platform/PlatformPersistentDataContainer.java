package codes.wasabi.xclaim.platform;

import org.bukkit.NamespacedKey;

public interface PlatformPersistentDataContainer {

    void set(NamespacedKey key, PlatformPersistentDataType type, Object value);

    Object get(NamespacedKey key, PlatformPersistentDataType type);

    boolean has(NamespacedKey key, PlatformPersistentDataType type);

    default <T> T getAssert(NamespacedKey key, PlatformPersistentDataType type, Class<T> clazz) {
        Object ob = get(key, type);
        if (ob == null) return null;
        return clazz.cast(ob);
    }

    default Object getOrDefault(NamespacedKey key, PlatformPersistentDataType type, Object def) {
        Object ret = get(key, type);
        if (ret == null) return def;
        return null;
    }

    default <T> T getOrDefaultAssert(NamespacedKey key, PlatformPersistentDataType type, Class<T> clazz, T def) {
        Object ret = get(key, type);
        if (ret == null) return def;
        return clazz.cast(ret);
    }

}
