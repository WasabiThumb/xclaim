package codes.wasabi.xclaim.platform.spigot;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformChatListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpigotPlatform extends Platform {

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
        Map<String, Object> map = is.serialize();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeInt(map.size());
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                oos.writeInt(keyBytes.length);
                oos.write(keyBytes);
                oos.writeObject(entry.getValue());
            }
        } catch (IOException ignored) { }
        return bos.toByteArray();
    }

    @Override
    public @NotNull ItemStack itemStackDeserializeBytes(byte @NotNull [] bytes) {
        Map<String, Object> map = new HashMap<>();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            int size = ois.readInt();
            for (int i=0; i < size; i++) {
                int len = ois.readInt();
                String key = new String(ois.readNBytes(len), StandardCharsets.UTF_8);
                Object value = ois.readObject();
                map.put(key, value);
            }
        } catch (IOException | ClassNotFoundException ignored) { }
        return ItemStack.deserialize(map);
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
}
