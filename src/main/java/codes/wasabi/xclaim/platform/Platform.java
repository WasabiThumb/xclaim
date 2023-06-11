package codes.wasabi.xclaim.platform;

import codes.wasabi.xclaim.XClaim;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public abstract class Platform {

    private static boolean initialized = false;
    private static Platform instance;
    private static BukkitAudiences adventure;

    public static void init() {
        if (initialized) return;
        boolean isPaper = PaperLib.isPaper();
        if (!isPaper) {
            if (!XClaim.mainConfig.getBoolean("disable-paper-warning", false)) {
                PaperLib.suggestPaper(XClaim.instance);
            }
        }
        if (PaperLib.isVersion(17)) {
            if (isFolio()) {
                instance = new codes.wasabi.xclaim.platform.folio_1_19.FolioPlatform();
            } else if (isPaper) {
                instance = new codes.wasabi.xclaim.platform.paper_1_17.PaperPlatform();
            } else {
                instance = new codes.wasabi.xclaim.platform.spigot_1_17.SpigotPlatform_1_17();
            }
        } else if (PaperLib.isVersion(16)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_16.SpigotPlatform_1_16();
        } else if (PaperLib.isVersion(15)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_15.SpigotPlatform_1_15();
        } else if (PaperLib.isVersion(14, 4)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_14_4.SpigotPlatform_1_14_4();
        } else if (PaperLib.isVersion(14)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_14.SpigotPlatform_1_14();
        } else if (PaperLib.isVersion(13, 2)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_13_2.SpigotPlatform_1_13_2();
        } else if (PaperLib.isVersion(13)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_13.SpigotPlatform_1_13();
        } else if (PaperLib.isVersion(12, 2)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_12_2.SpigotPlatform_1_12_2();
        } else if (PaperLib.isVersion(12)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_12.SpigotPlatform_1_12();
        } else if (PaperLib.isVersion(11)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_11.SpigotPlatform_1_11();
        } else if (PaperLib.isVersion(10)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_10.SpigotPlatform_1_10();
        } else if (PaperLib.isVersion(9)) {
            instance = new codes.wasabi.xclaim.platform.spigot_1_9.SpigotPlatform_1_9();
        } else {
            instance = new codes.wasabi.xclaim.platform.spigot_1_8.SpigotPlatform_1_8();
        }
        adventure = BukkitAudiences.create(XClaim.instance);
        initialized = true;
    }

    private static boolean isFolio() {
        boolean folio = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            folio = true;
        } catch (Throwable ignored) { }
        return folio;
    }

    public static void cleanup() {
        if (!initialized) return;
        instance = null;
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        initialized = false;
    }

    public static Platform get() {
        return instance;
    }

    public static BukkitAudiences getAdventure() {
        return adventure;
    }

    public abstract @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name);

    public abstract byte @NotNull [] itemStackSerializeBytes(@NotNull ItemStack is);

    public abstract @NotNull ItemStack itemStackDeserializeBytes(byte @NotNull [] bytes);

    public abstract void metaDisplayName(@NotNull ItemMeta meta, @Nullable Component name);

    public abstract void metaLore(@NotNull ItemMeta meta, @Nullable List<Component> lore);

    public abstract void closeInventory(@NotNull Inventory iv);

    public abstract @NotNull Inventory createInventory(@NotNull InventoryHolder holder, int size, @NotNull Component name);

    public abstract @NotNull PlatformChatListener onChat();

    public abstract @NotNull Component playerDisplayName(@NotNull Player ply);

    public abstract void sendActionBar(@NotNull Player ply, @NotNull Component text);

    public abstract long getLastSeen(@NotNull OfflinePlayer ply);

    public abstract @NotNull Location toCenterLocation(@NotNull Location loc);

    public abstract @Nullable Location getInteractionPoint(@NotNull PlayerInteractEvent event);

    public abstract int getWorldMinHeight(@NotNull World world);

    public abstract PlatformNamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name);

    public abstract Material getSpyglassMaterial();

    public abstract EnumSet<EntityType> getMiscTypes();

    public abstract @Nullable ItemStack getPlayerItemInUse(Player ply);

    public abstract boolean supportsArtificalElytraBoost();

    public abstract void artificialElytraBoost(Player ply, ItemStack is);

    public abstract @Nullable ItemStack playerInventoryGetItem(PlayerInventory inv, EquipmentSlot slot);

    public abstract boolean supportsArtificialBookOpen();

    public abstract void artificialBookOpen(Player ply, ItemStack book);

    public abstract void createExplosion(World w, Location loc, float power, boolean setFire, boolean breakBlocks, Entity source);

    public abstract PlatformPersistentDataContainer getPersistentDataContainer(Entity entity);

    public abstract Material getGreenToken();

    public abstract Material getRedToken();

    public abstract Material getYellowToken();

    public abstract Material getOrangeToken();

    public abstract Material getLimeToken();

    public abstract boolean hasPlaceListener();

    public abstract @Nullable PlatformEntityPlaceListener getPlaceListener();

    public @NotNull PlatformEntityPlaceListener getPlaceListenerAssert() throws NullPointerException {
        return Objects.requireNonNull(getPlaceListener());
    }

    public abstract String getApiVersion(PluginDescriptionFile descriptionFile);

    public abstract boolean worldKeepInventory(World world);

    public abstract Material getPlayerHeadMaterial();

    public abstract Material getGreenConcreteMaterial();

    public abstract Material getRedConcreteMaterial();

    public abstract Material getSkeletonSkullMaterial();

    public abstract Material getEnchantingTableMaterial();

    public abstract Material getChestMinecartMaterial();

    public abstract Material getCraftingTableMaterial();

    public abstract Material getFireworkRocketMaterial();

    public abstract Material[] getSoilMaterials();

    public abstract boolean hasFireChargeMaterial();

    public abstract Material getFireChargeMaterial();

    public abstract void setOwningPlayer(SkullMeta sm, OfflinePlayer player);

    public abstract boolean bukkitTaskCancelled(BukkitTask task);

    public abstract ItemStack preparePlayerSkull(ItemStack is);

    public abstract boolean materialIsItem(Material material);

    public abstract Sound getMagicSound();

    public abstract EquipmentSlot getInteractHand(PlayerInteractEvent event);

    public abstract Sound getClickSound();

    public abstract Sound getExpSound();

    public abstract Sound getEggSound();

    public abstract Sound getLevelSound();

    public abstract Material getShieldMaterial();

    public abstract boolean playerIsGliding(Player ply);

    public abstract PlatformItemPickupListener getItemPickupListener();

    public abstract PlatformScheduler getScheduler();

}
