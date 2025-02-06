package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

import java.util.Queue;

public class SpongePlatformPlayerDropItemEvent
        extends SpongePlatformEvent<DropItemEvent>
        implements PlatformPlayerDropItemEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull DropItemEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!handle.cause().containsType(Player.class)) return;
        queue.add(new SpongePlatformPlayerDropItemEvent(platform, handle));
    }

    //

    private SpongePlatformPlayerDropItemEvent(
            @NotNull SpongePlatform platform,
            @NotNull DropItemEvent handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
