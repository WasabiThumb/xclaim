package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.SpigotItemSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class SpigotPlatformItem extends BukkitPlatformItem {

    public SpigotPlatformItem(@NotNull SpigotPlatform platform, @NotNull ItemStack handle) {
        super(platform, handle);
    }

    protected @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) this.platform;
    }

    @Override
    public @NotNull String displayName() {
        String displayName = "";
        ItemMeta meta = this.handle.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(this.handle.getType());
        if (meta != null) {
            displayName = this.platform().mm().serialize(
                    LegacyComponentSerializer.legacySection().deserialize(meta.getDisplayName())
            );
        }
        return displayName;
    }

    @Override
    public SpigotPlatformItem displayName(@NotNull String displayName) {
        this.modifyMeta((ItemMeta m) -> m.setDisplayName(LegacyComponentSerializer.legacySection().serialize(
                this.platform().mm().deserialize(displayName)
        )));
        return this;
    }

    @Override
    public @NotNull List<String> lore() {
        List<String> lore = null;
        ItemMeta meta = this.handle.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(this.handle.getType());
        if (meta != null && (lore = meta.getLore()) != null) {
            List<String> cpy = new ArrayList<>(lore.size());
            for (String raw : lore)
                cpy.add(this.platform().mm().serialize(LegacyComponentSerializer.legacySection().deserialize(raw)));
            lore = cpy;
        }
        if (lore == null) return Collections.emptyList();
        return Collections.unmodifiableList(lore);
    }

    @Override
    @Contract("_ -> this")
    public @NotNull SpigotPlatformItem lore(@NotNull List<String> lore) {
        List<String> parsed = new ArrayList<>(lore.size());
        for (String rich : lore) {
            parsed.add(LegacyComponentSerializer.legacySection().serialize(this.platform().mm().deserialize(rich)));
        }
        this.modifyMeta((ItemMeta meta) -> meta.setLore(parsed));
        return this;
    }

    @Override
    public byte @NotNull [] toBytes() {
        return SpigotItemSerializer.serialize(this.handle);
    }

    @Override
    public boolean isConsumable() {
        return this.handle.getType().isEdible();
    }

}
