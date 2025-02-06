package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.event.helper.SpongePlatformInventoryEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;

public class SpongePlatformInventoryCloseEvent
        extends SpongePlatformInventoryEvent<InteractContainerEvent.Close>
        implements PlatformInventoryCloseEvent
{

    @Adapter
    public SpongePlatformInventoryCloseEvent(
            @NotNull SpongePlatform platform,
            @NotNull InteractContainerEvent.Close handle
    ) {
        super(platform, handle);
    }

}
