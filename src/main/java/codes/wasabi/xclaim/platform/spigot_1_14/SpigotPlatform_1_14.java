package codes.wasabi.xclaim.platform.spigot_1_14;

import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.spigot_1_13_2.SpigotPlatform_1_13_2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class SpigotPlatform_1_14 extends SpigotPlatform_1_13_2 {

    @Override
    public PlatformPersistentDataContainer getPersistentDataContainer(Entity entity) {
        return new SpigotPlatformPersistentDataContainer_1_14(entity);
    }

    @Override
    public Material getGreenToken() {
        return Material.GREEN_DYE;
    }

    @Override
    public Material getRedToken() {
        return Material.RED_DYE;
    }

    @Override
    public Material getYellowToken() {
        return Material.YELLOW_DYE;
    }

}
