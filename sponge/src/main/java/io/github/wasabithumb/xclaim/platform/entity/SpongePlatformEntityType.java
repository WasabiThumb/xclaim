package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
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
        DefaultedRegistryReference<EntityType<?>> ref = type.asDefaultedReference(RegistryTypes.ENTITY_TYPE);
        if (RegistryUtil.referenceEquals(ref, EntityTypes.PLAYER))      return NamedPlatformEntityType.PLAYER;
        if (RegistryUtil.referenceEquals(ref, EntityTypes.TNT))         return NamedPlatformEntityType.TNT;
        if (RegistryUtil.referenceEquals(ref, EntityTypes.CREEPER))     return NamedPlatformEntityType.CREEPER;
        if (RegistryUtil.referenceEquals(ref, EntityTypes.END_CRYSTAL)) return NamedPlatformEntityType.END_CRYSTAL;
        return new SpongePlatformEntityType(type);
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.key(RegistryTypes.ENTITY_TYPE).formatted();
    }

}
