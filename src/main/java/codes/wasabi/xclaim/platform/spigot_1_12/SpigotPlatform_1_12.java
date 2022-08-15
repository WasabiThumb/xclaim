package codes.wasabi.xclaim.platform.spigot_1_12;

import codes.wasabi.xclaim.platform.spigot_1_11.SpigotPlatform_1_11;
import org.bukkit.Material;

public class SpigotPlatform_1_12 extends SpigotPlatform_1_11 {

    @Override
    public boolean materialIsItem(Material material) {
        return material.isItem();
    }

    @Override
    public Material getGreenToken() {
        return Material.valueOf("GREEN_GLAZED_TERRACOTTA");
    }

    @Override
    public Material getRedToken() {
        return Material.valueOf("RED_GLAZED_TERRACOTTA");
    }

    @Override
    public Material getYellowToken() {
        return Material.valueOf("YELLOW_GLAZED_TERRACOTTA");
    }

    @Override
    public Material getOrangeToken() {
        return Material.valueOf("ORANGE_GLAZED_TERRACOTTA");
    }

    @Override
    public Material getLimeToken() {
        return Material.valueOf("LIME_GLAZED_TERRACOTTA");
    }

}
