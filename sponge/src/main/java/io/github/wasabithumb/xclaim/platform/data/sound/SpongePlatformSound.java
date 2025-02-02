package io.github.wasabithumb.xclaim.platform.data.sound;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

import java.util.HashMap;
import java.util.Map;

public record SpongePlatformSound(
        SoundType handle
) implements PlatformSound {

    private static SoundType adapt(NamedPlatformSound named) {
        return switch (named) {
            case EXP      -> SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP.get();
            case CLICK    -> SoundTypes.UI_BUTTON_CLICK.get();
            case LEVEL    -> SoundTypes.ENTITY_PLAYER_LEVELUP.get();
            case MAGIC    -> SoundTypes.BLOCK_ENCHANTMENT_TABLE_USE.get();
            case WILDCARD -> SoundTypes.ENTITY_GHAST_AMBIENT.get();
        };
    }

    public static SoundType adapt(PlatformSound sound) {
        if (sound instanceof NamedPlatformSound named) {
            return adapt(named);
        }
        return ((SpongePlatformSound) sound).handle();
    }

    private static final Map<SoundType, NamedPlatformSound> NAMED_BY_HANDLE = new HashMap<>();
    static {
        for (NamedPlatformSound named : NamedPlatformSound.values()) {
            NAMED_BY_HANDLE.put(adapt(named), named);
        }
    }

    public static PlatformSound adapt(SoundType handle) {
        NamedPlatformSound named = NAMED_BY_HANDLE.get(handle);
        if (named != null) return named;
        return new SpongePlatformSound(handle);
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.key().asString();
    }

}
