package codes.wasabi.xclaim.platform.paper_1_17;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformChatListener;
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

public class PaperPlatform extends Platform {

    @Override
    public @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return Bukkit.getOfflinePlayerIfCached(name);
    }

    @Override
    public byte @NotNull [] itemStackSerializeBytes(@NotNull ItemStack is) {
        return is.serializeAsBytes();
    }

    @Override
    public @NotNull ItemStack itemStackDeserializeBytes(byte @NotNull [] bytes) {
        return ItemStack.deserializeBytes(bytes);
    }

    @Override
    public void metaDisplayName(@NotNull ItemMeta meta, @Nullable Component name) {
        meta.displayName(name);
    }

    @Override
    public void metaLore(@NotNull ItemMeta meta, @Nullable List<Component> lore) {
        meta.lore(lore);
    }

    @Override
    public void closeInventory(@NotNull Inventory iv) {
        iv.close();
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder holder, int size, @NotNull Component name) {
        return Bukkit.createInventory(holder, size, name);
    }

    @Override
    public @NotNull PlatformChatListener onChat() {
        PlatformChatListener l = new PaperPlatformChatListener();
        Bukkit.getPluginManager().registerEvents(l, XClaim.instance);
        return l;
    }

    @Override
    public @NotNull Component playerDisplayName(@NotNull Player ply) {
        return ply.displayName();
    }

    @Override
    public void sendActionBar(@NotNull Player ply, @NotNull Component text) {
        ply.sendActionBar(text);
    }

    @Override
    public long getLastSeen(@NotNull OfflinePlayer ply) {
        return ply.getLastSeen();
    }

    @Override
    public @NotNull Location toCenterLocation(@NotNull Location loc) {
        return loc.toCenterLocation();
    }

    @Override
    public @Nullable Location getInteractionPoint(@NotNull PlayerInteractEvent event) {
        return event.getInteractionPoint();
    }

    // NEW
    @Override
    public int getWorldMinHeight(@NotNull World world) {
        return world.getMinHeight();
    }

    @Override
    public NamespacedKey createNamespacedKey(@NotNull JavaPlugin plugin, @NotNull String name) {
        return NamespacedKey.fromString(name, plugin);
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

}
