package codes.wasabi.xclaim.platform.spigot_1_8;

import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.PlatformItemPickupListener;
import codes.wasabi.xclaim.platform.PlatformNamespacedKey;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.spigot.SpigotPlatform;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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

public class SpigotPlatform_1_8 extends SpigotPlatform {

    @Override
    public PlatformNamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name) {
        return new SpigotPlatformNamespacedKey_1_8(plugin.getName().toLowerCase(Locale.ROOT), name.toLowerCase(Locale.ROOT));
    }

    @Override
    public int getWorldMinHeight(@NotNull World world) {
        return 0;
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
                    EntityType.ARROW,
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
                return inv.getItemInHand();
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
        return new SpigotPlatformPersistentDataContainer_1_8(entity);
    }

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new SpigotPlatformEntityPlaceListener_1_8();
    }

    // stale
    @Override
    public Material getGreenToken() {
        return Material.valueOf("GREEN_RECORD");
    }

    // stale
    @Override
    public Material getRedToken() {
        return Material.valueOf("RECORD_4");
    }

    // stale
    @Override
    public Material getYellowToken() {
        return Material.valueOf("GOLD_RECORD");
    }

    // stale
    @Override
    public Material getOrangeToken() {
        return Material.valueOf("RECORD_3");
    }

    // stale
    @Override
    public Material getLimeToken() {
        return Material.valueOf("GREEN_RECORD");
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
            method.setAccessible(true);
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

    @Override
    public boolean materialIsItem(Material material) {
        return !material.isBlock();
    }

    @Override
    public Sound getMagicSound() {
        return Sound.valueOf("ENDERMAN_TELEPORT");
    }

    @Override
    public EquipmentSlot getInteractHand(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) return null;
        return EquipmentSlot.HAND;
    }

    @Override
    public Sound getClickSound() {
        return Sound.valueOf("CLICK");
    }

    @Override
    public Sound getExpSound() {
        return Sound.valueOf("ORB_PICKUP");
    }

    @Override
    public Sound getEggSound() {
        return Sound.valueOf("GHAST_MOAN");
    }

    @Override
    public Sound getLevelSound() {
        return Sound.valueOf("LEVEL_UP");
    }

    @Override
    public Material getShieldMaterial() {
        return Material.CHAINMAIL_CHESTPLATE;
    }

    @Override
    public boolean playerIsGliding(Player ply) {
        return false;
    }

    @Override
    public PlatformItemPickupListener getItemPickupListener() {
        return new codes.wasabi.xclaim.platform.spigot_1_8.SpigotPlatformItemPickupListener_1_8();
    }

}
