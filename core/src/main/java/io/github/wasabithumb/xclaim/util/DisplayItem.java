package io.github.wasabithumb.xclaim.util;

import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DisplayItem {

    private static final String I_PREFIX = "<i:false>";
    private static final String I_SUFFIX = "</i:false>";

    public static @NotNull PlatformItem format(
            @NotNull PlatformItem item,
            @NotNull String name,
            @NotNull ColorTag color,
            @NotNull List<String> lore
    ) {
        return item
                .displayName(I_PREFIX + color.format(name) + I_SUFFIX)
                .lore(lore)
                .hideExtra();
    }

    public static @NotNull PlatformItem format(
            @NotNull PlatformItem item,
            @NotNull String name,
            @NotNull ColorTag color,
            @NotNull String @NotNull ... lore
    ) {
        return item
                .displayName(I_PREFIX + color.format(name) + I_SUFFIX)
                .lore(lore)
                .hideExtra();
    }

    public static @NotNull PlatformItem format(@NotNull PlatformItem item, @NotNull String name, @NotNull ColorTag color) {
        return format(item, name, color, new String[0]);
    }

    public static @NotNull PlatformItem format(
            @NotNull PlatformItem item,
            @NotNull String name,
            @NotNull String @NotNull ... lore
    ) {
        return format(item, name, ColorTag.WHITE, lore);
    }

    public static @NotNull PlatformItem format(
            @NotNull PlatformItem item,
            @NotNull String name,
            @NotNull List<String> lore
    ) {
        return format(item, name, ColorTag.WHITE, lore);
    }

    public static @NotNull PlatformItem format(@NotNull PlatformItem item, @NotNull String name) {
        return format(item, name, ColorTag.WHITE, new String[0]);
    }

}
