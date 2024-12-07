package io.github.wasabithumb.xclaim.platform.data.sound;

import org.bukkit.Sound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record BukkitPlatformSound(
        @NotNull Sound handle
) implements PlatformSound {

    public static @NotNull Sound parseNamed(@NotNull NamedPlatformSound named) {
        return switch (named) {
            case MAGIC    -> Sound.BLOCK_ENCHANTMENT_TABLE_USE;
            case CLICK    -> Sound.UI_BUTTON_CLICK;
            case EXP      -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            case LEVEL    -> Sound.ENTITY_PLAYER_LEVELUP;
            case WILDCARD -> Sound.ENTITY_GHAST_AMBIENT;
        };
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSound of(@NotNull Sound sound) {
        return switch (sound) {
            case BLOCK_ENCHANTMENT_TABLE_USE  -> NamedPlatformSound.MAGIC;
            case UI_BUTTON_CLICK              -> NamedPlatformSound.CLICK;
            case ENTITY_EXPERIENCE_ORB_PICKUP -> NamedPlatformSound.EXP;
            case ENTITY_PLAYER_LEVELUP        -> NamedPlatformSound.LEVEL;
            case ENTITY_GHAST_AMBIENT         -> NamedPlatformSound.WILDCARD;
            default -> new BukkitPlatformSound(sound);
        };
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.name();
    }

}
