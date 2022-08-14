package codes.wasabi.xclaim.platform.spigot_1_13;

import codes.wasabi.xclaim.platform.spigot_1_12_2.SpigotPlatform_1_12_2;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.EnumSet;

public class SpigotPlatform_1_13 extends SpigotPlatform_1_12_2 {

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
                    EntityType.TRIDENT,
                    EntityType.UNKNOWN
            );
        }
        return miscTypes;
    }

    @Override
    public Material getGreenToken() {
        return Material.valueOf("CACTUS_GREEN");
    }

    @Override
    public Material getRedToken() {
        return Material.valueOf("ROSE_RED");
    }

    @Override
    public Material getYellowToken() {
        return Material.valueOf("DANDELION_YELLOW");
    }

    @Override
    public Material getOrangeToken() {
        return Material.ORANGE_DYE;
    }

    @Override
    public Material getLimeToken() {
        return Material.LIME_DYE;
    }

    @Override
    public String getApiVersion(PluginDescriptionFile descriptionFile) {
        return descriptionFile.getAPIVersion();
    }

    @Override
    public boolean worldKeepInventory(World world) {
        boolean keepInventory = false;
        Boolean value = world.getGameRuleValue(GameRule.KEEP_INVENTORY);
        if (value == null) value = world.getGameRuleDefault(GameRule.KEEP_INVENTORY);
        if (value != null) keepInventory = value;
        return keepInventory;
    }

    @Override
    public Material getPlayerHeadMaterial() {
        return Material.PLAYER_HEAD;
    }

    @Override
    public Material getGreenConcreteMaterial() {
        return Material.GREEN_CONCRETE;
    }

    @Override
    public Material getRedConcreteMaterial() {
        return Material.RED_CONCRETE;
    }

    @Override
    public Material getSkeletonSkullMaterial() {
        return Material.SKELETON_SKULL;
    }

    @Override
    public Material getEnchantingTableMaterial() {
        return Material.ENCHANTING_TABLE;
    }

    @Override
    public Material getChestMinecartMaterial() {
        return Material.CHEST_MINECART;
    }

    @Override
    public Material getCraftingTableMaterial() {
        return Material.CRAFTING_TABLE;
    }

    @Override
    public Material getFireworkRocketMaterial() {
        return Material.FIREWORK_ROCKET;
    }

    @Override
    public Material[] getSoilMaterials() {
        return new Material[] {
                Material.LEGACY_SOIL,
                Material.FARMLAND
        };
    }

    @Override
    public boolean hasFireChargeMaterial() {
        return true;
    }

    @Override
    public Material getFireChargeMaterial() {
        return Material.FIRE_CHARGE;
    }

    @Override
    public ItemStack preparePlayerSkull(ItemStack is) {
        return is;
    }

}
