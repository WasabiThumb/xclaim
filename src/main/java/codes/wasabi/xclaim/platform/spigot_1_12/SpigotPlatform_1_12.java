package codes.wasabi.xclaim.platform.spigot_1_12;

import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.spigot.SpigotPlatform;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Locale;

public class SpigotPlatform_1_12 extends SpigotPlatform {

    @Override
    public int getWorldMinHeight(@NotNull World world) {
        return 0;
    }

    @Override
    public NamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name) {
        return new NamespacedKey(plugin, name);
    }

    @Override
    public Material getSpyglassMaterial() {
        return Material.GLASS_BOTTLE;
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
    public @Nullable ItemStack getPlayerItemInUse(Player ply) {
        PlayerInventory inv = ply.getInventory();
        int slot = inv.getHeldItemSlot();
        return inv.getItem(slot);
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
    public boolean supportsArtificialBookOpen() {
        return false;
    }

    @Override
    public void artificialBookOpen(Player ply, ItemStack book) {

    }

    @Override
    public void createExplosion(World w, Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
        w.createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks);
    }

    @Override
    public PlatformPersistentDataContainer getPersistentDataContainer(Entity entity) {
        return new SpigotPlatformPersistentDataContainer_1_12(entity);
    }

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new SpigotPlatformEntityPlaceListener_1_12();
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

    @Override
    public String getApiVersion(PluginDescriptionFile descriptionFile) {
        return "1.13";
    }

    @Override
    public boolean worldKeepInventory(World world) {
        String rule = world.getGameRuleValue("keepInventory");
        boolean ret = false;
        if (rule != null) {
            rule = rule.toLowerCase(Locale.ROOT);
            if (rule.equals("true") || rule.equals("1")) ret = true;
        }
        return ret;
    }

    @Override
    public Material getPlayerHeadMaterial() {
        return Material.valueOf("SKULL_ITEM");
    }

    @Override
    public Material getGreenConcreteMaterial() {
        return Material.EMERALD_BLOCK;
    }

    @Override
    public Material getRedConcreteMaterial() {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public Material getSkeletonSkullMaterial() {
        return Material.valueOf("SKULL_ITEM");
    }

    @Override
    public Material getEnchantingTableMaterial() {
        return Material.valueOf("ENCHANTMENT_TABLE");
    }

    @Override
    public Material getChestMinecartMaterial() {
        return Material.valueOf("STORAGE_MINECART");
    }

    @Override
    public Material getCraftingTableMaterial() {
        return Material.valueOf("WORKBENCH");
    }

    @Override
    public Material getFireworkRocketMaterial() {
        return Material.valueOf("FIREWORK");
    }

    @Override
    public Material[] getSoilMaterials() {
        return new Material[] {
                Material.valueOf("SOIL")
        };
    }

    @Override
    public boolean hasFireChargeMaterial() {
        return false;
    }

    @Override
    public Material getFireChargeMaterial() {
        return Material.valueOf("FIRE");
    }

    @Override
    public void setOwningPlayer(SkullMeta sm, OfflinePlayer player) {
        String name = player.getName();
        if (name == null) name = player.getUniqueId().toString();
        Class<? extends SkullMeta> clazz = sm.getClass();
        try {
            Method method = clazz.getMethod("setOwner", String.class);
            method.invoke(sm, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean bukkitTaskCancelled(BukkitTask task) {
        return false;
    }

    @Override
    public ItemStack preparePlayerSkull(ItemStack is) {
        is.setDurability((short) 3);
        return is;
    }

}
