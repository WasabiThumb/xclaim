package codes.wasabi.xclaim.platform.spigot_1_12;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import codes.wasabi.xclaim.platform.spigot.SpigotPlatform;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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

    private PlatformPersistentDataContainer createNBTApiPDC(Entity entity) {
        de.tr7zw.nbtapi.NBTEntity nbt = new de.tr7zw.nbtapi.NBTEntity(entity);
        return new PlatformPersistentDataContainer() {
            @Override
            public void set(NamespacedKey key, PlatformPersistentDataType type, Object value) {
                switch (type) {
                    case BYTE:
                        nbt.setByte(key.toString(), (Byte) value);
                        break;
                    case BYTE_ARRAY:
                        nbt.setByteArray(key.toString(), (byte[]) value);
                        break;
                    case STRING:
                        nbt.setString(key.toString(), (String) value);
                        break;
                }
            }

            @Override
            public Object get(NamespacedKey key, PlatformPersistentDataType type) {
                Object ret = null;
                switch (type) {
                    case BYTE:
                        ret = nbt.getByte(key.toString());
                        break;
                    case BYTE_ARRAY:
                        ret = nbt.getByteArray(key.toString());
                        break;
                    case STRING:
                        ret = nbt.getString(key.toString());
                        break;
                }
                return ret;
            }

            @Override
            public boolean has(NamespacedKey key, PlatformPersistentDataType type) {
                return nbt.hasKey(key.toString());
            }
        };
    }

    @Override
    public PlatformPersistentDataContainer getPersistentDataContainer(Entity entity) {
        return createNBTApiPDC(entity);
    }

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    private static class OldPlatformEntityPlaceListener extends PlatformEntityPlaceListener implements Listener {
        public OldPlatformEntityPlaceListener() {
            Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent event) {
            Player ply = event.getPlayer();
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                EquipmentSlot es = event.getHand();
                if (es == null) es = EquipmentSlot.HAND;
                ItemStack is = Platform.get().playerInventoryGetItem(ply.getInventory(), es);
                if (is == null) return;
                Material material = is.getType();
                String matName = material.name().toUpperCase(Locale.ROOT);
                Data data = new Data();
                data.cancel = () -> event.setCancelled(true);
                data.player = ply;
                Location interactPos;
                Block b = event.getClickedBlock();
                if (b != null) {
                    BlockFace face = event.getBlockFace();
                    interactPos = Platform.get().toCenterLocation(b.getRelative(face).getLocation());
                } else {
                    interactPos = ply.getLocation();
                }
                data.location = interactPos;
                if (matName.contains("MINECART") || matName.contains("BOAT")) {
                    data.isVehicle = true;
                    call(data);
                } else if (matName.contains("ARMOR_STAND") || matName.contains("PAINTING") || matName.contains("ITEM_FRAME") || matName.contains("SPAWN_EGG")) {
                    data.isVehicle = false;
                    call(data);
                }
            }
        }

        @Override
        protected void onUnregister() {
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new OldPlatformEntityPlaceListener();
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
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public Material getRedConcreteMaterial() {
        return Material.EMERALD_BLOCK;
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

    }

    @Override
    public boolean bukkitTaskCancelled(BukkitTask task) {
        return false;
    }

}
