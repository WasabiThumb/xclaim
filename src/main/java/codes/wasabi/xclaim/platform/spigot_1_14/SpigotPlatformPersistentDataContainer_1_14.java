package codes.wasabi.xclaim.platform.spigot_1_14;

import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpigotPlatformPersistentDataContainer_1_14 implements PlatformPersistentDataContainer {

    private final PersistentDataContainer pdc;

    public SpigotPlatformPersistentDataContainer_1_14(Entity entity) {
        pdc = entity.getPersistentDataContainer();
    }

    @Override
    public void set(NamespacedKey key, PlatformPersistentDataType type, Object value) {
        switch (type) {
            case BYTE:
                pdc.set(key, PersistentDataType.BYTE, (Byte) value);
                break;
            case BYTE_ARRAY:
                pdc.set(key, PersistentDataType.BYTE_ARRAY, (byte[]) value);
                break;
            case STRING:
                pdc.set(key, PersistentDataType.STRING, (String) value);
                break;
        }
    }

    @Override
    public Object get(NamespacedKey key, PlatformPersistentDataType type) {
        switch (type) {
            case BYTE:
                return pdc.get(key, PersistentDataType.BYTE);
            case BYTE_ARRAY:
                return pdc.get(key, PersistentDataType.BYTE_ARRAY);
            case STRING:
                return pdc.get(key, PersistentDataType.STRING);
        }
        return null;
    }

    @Override
    public boolean has(NamespacedKey key, PlatformPersistentDataType type) {
        switch (type) {
            case BYTE:
                return pdc.has(key, PersistentDataType.BYTE);
            case BYTE_ARRAY:
                return pdc.has(key, PersistentDataType.BYTE_ARRAY);
            case STRING:
                return pdc.has(key, PersistentDataType.STRING);
        }
        return false;
    }

}
