package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.SpongePlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.event.helper.SpongePlatformInventoryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;

public class SpongePlatformInventoryDragEvent
        extends SpongePlatformInventoryEvent<ClickContainerEvent.Drag>
        implements PlatformInventoryDragEvent
{

    @Adapter
    public SpongePlatformInventoryDragEvent(
            @NotNull SpongePlatform platform,
            @NotNull ClickContainerEvent.Drag handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @Nullable PlatformPlayer player() {
        SpongePlatformTypeAdapter adapter = this.platform.adapter();
        return this.handle.cause()
                .first(Player.class)
                .map(adapter::player)
                .orElse(null);
    }

}
