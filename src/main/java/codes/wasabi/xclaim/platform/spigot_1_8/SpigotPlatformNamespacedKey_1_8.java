package codes.wasabi.xclaim.platform.spigot_1_8;

import codes.wasabi.xclaim.platform.PlatformNamespacedKey;

public class SpigotPlatformNamespacedKey_1_8 extends PlatformNamespacedKey {

    private final String namespace;
    private final String key;
    public SpigotPlatformNamespacedKey_1_8(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getKey() {
        return key;
    }

}
