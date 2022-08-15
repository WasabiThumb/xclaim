package codes.wasabi.xclaim.platform.spigot_1_17;

import codes.wasabi.xclaim.platform.PlatformNamespacedKey;
import codes.wasabi.xclaim.platform.spigot_1_12.SpigotPlatformNamespacedKey_1_12;
import codes.wasabi.xclaim.platform.spigot_1_16.SpigotPlatform_1_16;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpigotPlatform_1_17 extends SpigotPlatform_1_16 {

    @Override
    public int getWorldMinHeight(@NotNull World world) {
        return world.getMinHeight();
    }

    @Override
    public PlatformNamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name) {
        return new SpigotPlatformNamespacedKey_1_12(NamespacedKey.fromString(name, plugin));
    }

    @Override
    public Material getSpyglassMaterial() {
        return Material.SPYGLASS;
    }

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
                    EntityType.MARKER,
                    EntityType.SMALL_FIREBALL,
                    EntityType.SNOWBALL,
                    EntityType.SPECTRAL_ARROW,
                    EntityType.SPLASH_POTION,
                    EntityType.THROWN_EXP_BOTTLE,
                    EntityType.TRIDENT,
                    EntityType.UNKNOWN
            );
        }
        return miscTypes;
    }

    @Override
    public @Nullable ItemStack getPlayerItemInUse(Player ply) {
        return ply.getItemInUse();
    }

    @Override
    public void createExplosion(World w, Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
        w.createExplosion(loc, power, setFire, breakBlocks, source);
    }

}
