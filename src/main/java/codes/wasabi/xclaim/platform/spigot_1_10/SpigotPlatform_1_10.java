package codes.wasabi.xclaim.platform.spigot_1_10;

import codes.wasabi.xclaim.platform.spigot_1_9.SpigotPlatform_1_9;
import org.bukkit.Sound;

public class SpigotPlatform_1_10 extends SpigotPlatform_1_9 {

    @Override
    public Sound getMagicSound() {
        return Sound.BLOCK_ENCHANTMENT_TABLE_USE;
    }

}
