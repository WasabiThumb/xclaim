package codes.wasabi.xclaim.platform;

import codes.wasabi.xclaim.XClaim;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public abstract class Platform {

    private static boolean initialized = false;
    private static Platform instance;
    private static BukkitAudiences adventure;

    public static void init() {
        if (initialized) return;
        boolean isNew = PaperLib.isVersion(17);
        boolean isPaper = PaperLib.isPaper();
        if (!isPaper) {
            PaperLib.suggestPaper(XClaim.instance);
        }
        if (isNew) {
            if (isPaper) {
                instance = new codes.wasabi.xclaim.platform.paper_1_17.PaperPlatform();
            } else {
                instance = new codes.wasabi.xclaim.platform.spigot_1_17.SpigotPlatform_1_17();
            }
        } else {
            instance = new codes.wasabi.xclaim.platform.spigot_1_16.SpigotPlatform_1_16();
        }
        adventure = BukkitAudiences.create(XClaim.instance);
        initialized = true;
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

    public abstract NamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name);

    public abstract Material getSpyglassMaterial();

    public abstract EnumSet<EntityType> getMiscTypes();

    public abstract @Nullable ItemStack getPlayerItemInUse(Player ply);

}
