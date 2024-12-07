package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ColorTag {
    BLACK,
    DARK_GRAY,
    DARK_BLUE,
    BLUE,
    DARK_GREEN,
    GREEN,
    DARK_AQUA,
    AQUA,
    DARK_RED,
    RED,
    DARK_PURPLE,
    LIGHT_PURPLE,
    GOLD,
    YELLOW,
    GRAY,
    WHITE;

    private final String tag;
    ColorTag(@Nullable String tag) {
        this.tag = (tag == null) ? this.name().toLowerCase(Locale.ROOT) : tag;
    }

    ColorTag() {
        this(null);
    }

    public @NotNull String tag() {
        return this.tag;
    }

    public @NotNull String format(@NotNull String text) {
        return "<" + this.tag + ">" + text + "</" + this.tag + ">";
    }

}
