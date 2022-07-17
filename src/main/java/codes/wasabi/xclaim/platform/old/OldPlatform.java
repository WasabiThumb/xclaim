package codes.wasabi.xclaim.platform.old;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformChatListener;
import codes.wasabi.xclaim.platform.spigot.SpigotPlatformChatListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class OldPlatform extends Platform {

    public OldPlatform() { }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            String n = op.getName();
            if (n == null) continue;
            if (n.equals(name)) return op;
        }
        return null;
    }

    @Override
    public byte @NotNull [] itemStackSerializeBytes(@NotNull ItemStack is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream oos = new BukkitObjectOutputStream(bos)) {
            oos.writeObject(is);
        } catch (IOException ignored) { }
        return bos.toByteArray();
    }

    @Override
    public @NotNull ItemStack itemStackDeserializeBytes(byte @NotNull [] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ItemStack is = null;
        try (BukkitObjectInputStream ois = new BukkitObjectInputStream(bis)) {
            Object ob = ois.readObject();
            is = (ItemStack) ob;
        } catch (IOException | ClassNotFoundException ignored) {}
        return Objects.requireNonNull(is);
    }

    @Override
    public void metaDisplayName(@NotNull ItemMeta meta, @Nullable Component name) {
        meta.setDisplayName(LegacyComponentSerializer.legacySection().serializeOrNull(name));
    }

    @Override
    public void metaLore(@NotNull ItemMeta meta, @Nullable List<Component> lore) {
        if (lore == null) {
            meta.setLore(null);
        } else {
            LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
            List<String> list = new ArrayList<>();
            for (Component c : lore) {
                list.add(legacy.serializeOrNull(c));
            }
            meta.setLore(list);
        }
    }

    @Override
    public void closeInventory(@NotNull Inventory iv) {
        for (HumanEntity he : new ArrayList<>(iv.getViewers())) {
            he.closeInventory();
        }
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull InventoryHolder holder, int size, @NotNull Component name) {
        return Bukkit.createInventory(holder, size, LegacyComponentSerializer.legacySection().serializeOrNull(name));
    }

    @Override
    public @NotNull PlatformChatListener onChat() {
        SpigotPlatformChatListener listener = new SpigotPlatformChatListener();
        Bukkit.getPluginManager().registerEvents(listener, XClaim.instance);
        return listener;
    }

    @Override
    public @NotNull Component playerDisplayName(@NotNull Player ply) {
        return Component.text(ply.getDisplayName());
    }

    @Override
    public void sendActionBar(@NotNull Player ply, @NotNull Component text) {
        getAdventure().player(ply).sendActionBar(text);
    }

    @Override
    public long getLastSeen(@NotNull OfflinePlayer ply) {
        if (ply.isOnline()) return System.currentTimeMillis();
        return ply.getLastPlayed();
    }

    @Override
    public @NotNull Location toCenterLocation(@NotNull Location loc) {
        World w = loc.getWorld();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return new Location(
                w,
                Math.floor(x) + 0.5d,
                Math.floor(y) + 0.5d,
                Math.floor(z) + 0.5d,
                loc.getYaw(),
                loc.getPitch()
        );
    }

    @Override
    public @Nullable Location getInteractionPoint(@NotNull PlayerInteractEvent event) {
        return null;
    }

    // NEW
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
                    EntityType.TRIDENT,
                    EntityType.UNKNOWN
            );
        }
        return miscTypes;
    }

    @Override
    public @Nullable ItemStack getPlayerItemInUse(Player ply) {
        return ply.getActiveItem();
    }

}
