package codes.wasabi.xclaim.platform;

public interface PlatformPersistentDataContainer {

    void set(PlatformNamespacedKey key, PlatformPersistentDataType type, Object value);

    Object get(PlatformNamespacedKey key, PlatformPersistentDataType type);

    boolean has(PlatformNamespacedKey key, PlatformPersistentDataType type);

    default <T> T getAssert(PlatformNamespacedKey key, PlatformPersistentDataType type, Class<T> clazz) {
        Object ob = get(key, type);
        if (ob == null) return null;
        return clazz.cast(ob);
    }

    default Object getOrDefault(PlatformNamespacedKey key, PlatformPersistentDataType type, Object def) {
        Object ret = get(key, type);
        if (ret == null) return def;
        return null;
    }

    default <T> T getOrDefaultAssert(PlatformNamespacedKey key, PlatformPersistentDataType type, Class<T> clazz, T def) {
        Object ret = get(key, type);
        if (ret == null) return def;
        return clazz.cast(ret);
    }

}
