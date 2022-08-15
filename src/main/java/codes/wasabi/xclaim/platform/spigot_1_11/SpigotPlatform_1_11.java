package codes.wasabi.xclaim.platform.spigot_1_11;

import codes.wasabi.xclaim.platform.spigot_1_10.SpigotPlatform_1_10;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;

public class SpigotPlatform_1_11 extends SpigotPlatform_1_10 {

    private EnumSet<EntityType> miscTypes = null;
    @Override
    public EnumSet<EntityType> getMiscTypes() {
        if (miscTypes == null) {
            miscTypes = EnumSet.of(
                    EntityType.AREA_EFFECT_CLOUD,
                    EntityType.ARROW,
                    EntityType.DRAGON_FIREBALL,
                    EntityType.DROPPED_ITEM,
                    EntityType.EGG,
                    EntityType.ENDER_CRYSTAL,
                    EntityType.ENDER_PEARL,
                    EntityType.ENDER_SIGNAL,
                    EntityType.EVOKER_FANGS,
                    EntityType.EXPERIENCE_ORB,
                    EntityType.FALLING_BLOCK,
                    EntityType.FIREBALL,
                    EntityType.FIREWORK,
                    EntityType.FISHING_HOOK,
                    EntityType.LIGHTNING,
                    EntityType.LLAMA_SPIT,
                    EntityType.SMALL_FIREBALL,
                    EntityType.SNOWBALL,
                    EntityType.SPECTRAL_ARROW,
                    EntityType.SPLASH_POTION,
                    EntityType.THROWN_EXP_BOTTLE,
                    EntityType.UNKNOWN
            );
        }
        return miscTypes;
    }

}
