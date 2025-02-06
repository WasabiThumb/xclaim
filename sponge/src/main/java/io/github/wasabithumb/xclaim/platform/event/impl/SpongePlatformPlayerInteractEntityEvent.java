package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import java.util.Queue;

public class SpongePlatformPlayerInteractEntityEvent
        extends SpongePlatformEvent<InteractEntityEvent.Secondary>
        implements PlatformPlayerInteractEntityEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull InteractEntityEvent.Secondary handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!handle.cause().containsType(Player.class)) return;
        queue.add(new SpongePlatformPlayerInteractEntityEvent(platform, handle));
    }

    //

    private SpongePlatformPlayerInteractEntityEvent(
            @NotNull SpongePlatform platform,
            @NotNull InteractEntityEvent.Secondary handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @UnknownNullability PlatformEquipmentSlot hand() {
        return this.inferHand();
    }

    @Override
    public @NotNull PlatformEntity interacted() {
        return this.platform.adapter().entity(this.handle.entity());
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
