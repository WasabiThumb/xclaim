package codes.wasabi.xclaim.platform.spigot_1_12;

import codes.wasabi.xclaim.platform.PlatformNamespacedKey;
import org.bukkit.NamespacedKey;

public class SpigotPlatformNamespacedKey_1_12 extends PlatformNamespacedKey {

    private final NamespacedKey nk;
    public SpigotPlatformNamespacedKey_1_12(NamespacedKey nk) {
        this.nk = nk;
    }

    public final NamespacedKey getBukkitNamespacedKey() {
        return nk;
    }

    @Override
    public String getNamespace() {
        return nk.getNamespace();
    }

    @Override
    public String getKey() {
        return nk.getKey();
    }

    @Override
    public String toString() {
        return nk.toString();
    }

}
