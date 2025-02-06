package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.ConstructEntityEvent;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Queue;

public class SpongePlatformEntityPlaceEvent
        extends SpongePlatformEvent<ConstructEntityEvent.Pre>
        implements PlatformEntityPlaceEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull ConstructEntityEvent.Pre handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!handle.cause().containsType(Player.class)) return;
        queue.add(new SpongePlatformEntityPlaceEvent(platform, handle));
    }

    //

    private SpongePlatformEntityPlaceEvent(
            @NotNull SpongePlatform platform,
            @NotNull ConstructEntityEvent.Pre handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformLocation location() {
        return this.platform.adapter().location(this.handle.location(), this.handle.rotation());
    }

    @Override
    public boolean isVehicle() {
        return RegistryUtil.referenceEquals(RegistryTypes.ENTITY_TYPE, this.handle.targetType(), EntityTypes.MINECART) ||
                RegistryUtil.referenceEquals(RegistryTypes.ENTITY_TYPE, this.handle.targetType(), EntityTypes.BOAT);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
