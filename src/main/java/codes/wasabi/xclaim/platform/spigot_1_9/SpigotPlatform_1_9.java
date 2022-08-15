package codes.wasabi.xclaim.platform.spigot_1_9;

import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.spigot_1_8.SpigotPlatform_1_8;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SpigotPlatform_1_9 extends SpigotPlatform_1_8 {

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
                    EntityType.EXPERIENCE_ORB,
                    EntityType.FALLING_BLOCK,
                    EntityType.FIREBALL,
                    EntityType.FIREWORK,
                    EntityType.FISHING_HOOK,
                    EntityType.LIGHTNING,
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

    @Override
    public @Nullable ItemStack playerInventoryGetItem(PlayerInventory inv, EquipmentSlot slot) {
        switch (slot) {
            case HAND:
                return inv.getItemInMainHand();
            case OFF_HAND:
                return inv.getItemInOffHand();
            case HEAD:
                return inv.getHelmet();
            case CHEST:
                return inv.getChestplate();
            case LEGS:
                return inv.getLeggings();
            case FEET:
                return inv.getBoots();
            default:
                return null;
        }
    }

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new SpigotPlatformEntityPlaceListener_1_9();
    }

    @Override
    public Sound getMagicSound() {
        return Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
    }

    @Override
    public EquipmentSlot getInteractHand(PlayerInteractEvent event) {
        return event.getHand();
    }

    @Override
    public Sound getClickSound() {
        return Sound.UI_BUTTON_CLICK;
    }

    @Override
    public Sound getExpSound() {
        return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    }

    @Override
    public Sound getEggSound() {
        return Sound.ENTITY_GHAST_AMBIENT;
    }

    @Override
    public Sound getLevelSound() {
        return Sound.ENTITY_PLAYER_LEVELUP;
    }

    @Override
    public Material getShieldMaterial() {
        return Material.SHIELD;
    }

    @Override
    public boolean playerIsGliding(Player ply) {
        return ply.isGliding();
    }

}
