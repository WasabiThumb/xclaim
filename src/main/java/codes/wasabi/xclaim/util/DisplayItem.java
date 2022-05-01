package codes.wasabi.xclaim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DisplayItem {

    private static final Map<TextDecoration, TextDecoration.State> clearDecorations = new HashMap<>();
    static {
        for (TextDecoration td : TextDecoration.values()) {
            clearDecorations.put(td, TextDecoration.State.FALSE);
        }
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull List<Component> lore, int count) {
        ItemStack is = new ItemStack(material, count);
        ItemMeta meta = is.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);
        meta.displayName(name);
        meta.lore(lore);
        is.setItemMeta(meta);
        return is;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull List<Component> lore) {
        return create(material, name, lore, 1);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int count) {
        return create(material, name, Collections.emptyList(), count);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name) {
        return create(material, name, Collections.emptyList(), 1);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String name, @NotNull TextColor color, int count) {
        return create(material, Component.text(name).color(color).decorations(clearDecorations), Collections.emptyList(), count);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String name, @NotNull TextColor color) {
        return create(material, Component.text(name).color(color).decorations(clearDecorations), Collections.emptyList(), 1);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String name, int count) {
        return create(material, Component.text(name).decorations(clearDecorations), Collections.emptyList(), count);
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String name) {
        return create(material, Component.text(name).decorations(clearDecorations), Collections.emptyList(), 1);
    }

}
