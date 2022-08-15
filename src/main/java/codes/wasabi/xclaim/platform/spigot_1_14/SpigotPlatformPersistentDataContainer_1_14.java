package codes.wasabi.xclaim.platform.spigot_1_14;

import codes.wasabi.xclaim.platform.PlatformNamespacedKey;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import codes.wasabi.xclaim.platform.spigot_1_12.SpigotPlatformNamespacedKey_1_12;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpigotPlatformPersistentDataContainer_1_14 implements PlatformPersistentDataContainer {

    private final PersistentDataContainer pdc;

    public SpigotPlatformPersistentDataContainer_1_14(Entity entity) {
        pdc = entity.getPersistentDataContainer();
    }

    private NamespacedKey convert(PlatformNamespacedKey pnk) {
        if (pnk instanceof SpigotPlatformNamespacedKey_1_12) {
            return ((SpigotPlatformNamespacedKey_1_12) pnk).getBukkitNamespacedKey();
        }
        return NamespacedKey.fromString(pnk.toString());
    }

    @Override
    public void set(PlatformNamespacedKey key, PlatformPersistentDataType type, Object value) {
        switch (type) {
            case BYTE:
                pdc.set(convert(key), PersistentDataType.BYTE, (Byte) value);
                break;
            case BYTE_ARRAY:
                pdc.set(convert(key), PersistentDataType.BYTE_ARRAY, (byte[]) value);
                break;
            case STRING:
                pdc.set(convert(key), PersistentDataType.STRING, (String) value);
                break;
        }
    }

    @Override
    public Object get(PlatformNamespacedKey key, PlatformPersistentDataType type) {
        switch (type) {
            case BYTE:
                return pdc.get(convert(key), PersistentDataType.BYTE);
            case BYTE_ARRAY:
                return pdc.get(convert(key), PersistentDataType.BYTE_ARRAY);
            case STRING:
                return pdc.get(convert(key), PersistentDataType.STRING);
        }
        return null;
    }

    @Override
    public boolean has(PlatformNamespacedKey key, PlatformPersistentDataType type) {
        switch (type) {
            case BYTE:
                return pdc.has(convert(key), PersistentDataType.BYTE);
            case BYTE_ARRAY:
                return pdc.has(convert(key), PersistentDataType.BYTE_ARRAY);
            case STRING:
                return pdc.has(convert(key), PersistentDataType.STRING);
        }
        return false;
    }

}
