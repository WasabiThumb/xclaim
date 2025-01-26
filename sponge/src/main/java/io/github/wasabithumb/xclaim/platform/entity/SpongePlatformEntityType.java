package io.github.wasabithumb.xclaim.platform.entity;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryTypes;

public record SpongePlatformEntityType(
        @NotNull EntityType<?> handle
) implements PlatformEntityType {

    public static @NotNull EntityType<?> parseNamed(@NotNull NamedPlatformEntityType named) {
        return switch (named) {
            case PLAYER -> EntityTypes.PLAYER.get();
            case TNT -> EntityTypes.TNT.get();
            case CREEPER -> EntityTypes.CREEPER.get();
            case END_CRYSTAL -> EntityTypes.END_CRYSTAL.get();
        };
    }

    public static @NotNull PlatformEntityType of(@NotNull EntityType<?> type) {
        if (type == EntityTypes.PLAYER.get()) return NamedPlatformEntityType.PLAYER;
        if (type == EntityTypes.TNT.get()) return NamedPlatformEntityType.TNT;
        if (type == EntityTypes.CREEPER.get()) return NamedPlatformEntityType.CREEPER;
        if (type == EntityTypes.END_CRYSTAL.get()) return NamedPlatformEntityType.END_CRYSTAL;
        return new SpongePlatformEntityType(type);
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.key(RegistryTypes.ENTITY_TYPE).formatted();
    }

}
