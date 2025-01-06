package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ColorTag {
    BLACK(       0x000000),
    DARK_GRAY(   0x555555),
    DARK_BLUE(   0x0000aa),
    BLUE(        0x5555ff),
    DARK_GREEN(  0x00aa00),
    GREEN(       0x55ff55),
    DARK_AQUA(   0x00aaaa),
    AQUA(        0x55ffff),
    DARK_RED(    0xaa0000),
    RED(         0xff5555),
    DARK_PURPLE( 0xaa00aa),
    LIGHT_PURPLE(0xff55ff),
    GOLD(        0xffaa00),
    YELLOW(      0xffff55),
    GRAY(        0xaaaaaa),
    WHITE(       0xffffff);

    private final int rgb;
    ColorTag(int rgb) {
        this.rgb = rgb;
    }

    public int rgb() {
        return this.rgb;
    }

    public @NotNull String tag() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public @NotNull String format(@NotNull String text) {
        final String tag = this.tag();
        return "<" + tag + ">" + text + "</" + tag + ">";
    }

}
