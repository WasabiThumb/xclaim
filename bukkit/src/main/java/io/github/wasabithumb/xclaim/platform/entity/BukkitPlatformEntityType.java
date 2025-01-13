package io.github.wasabithumb.xclaim.platform.entity;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record BukkitPlatformEntityType(
        @NotNull EntityType handle
) implements PlatformEntityType {

    public static @NotNull EntityType parseNamed(@NotNull NamedPlatformEntityType named) {
        return switch (named) {
            case PLAYER -> EntityType.PLAYER;
            case CREEPER -> EntityType.CREEPER;
            case END_CRYSTAL -> EntityType.ENDER_CRYSTAL;
            case TNT -> EntityType.PRIMED_TNT;
        };
    }

    public static @NotNull PlatformEntityType of(@NotNull EntityType type) {
        if (type == EntityType.PLAYER) return NamedPlatformEntityType.PLAYER;
        if (type == EntityType.CREEPER) return NamedPlatformEntityType.CREEPER;
        if (type == EntityType.ENDER_CRYSTAL) return NamedPlatformEntityType.END_CRYSTAL;
        if (type == EntityType.PRIMED_TNT) return NamedPlatformEntityType.TNT;
        return new BukkitPlatformEntityType(type);
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.name();
    }

}
