package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.Queue;

public class SpongePlatformEntityPickupItemEvent
        extends SpongePlatformEvent<ChangeInventoryEvent.Pickup.Pre>
        implements PlatformEntityPickupItemEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull ChangeInventoryEvent.Pickup.Pre handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (handle.inventory() instanceof CarriedInventory<?> carried) {
            Carrier c = carried.carrier().orElse(null);
            if (c instanceof Entity entity) {
                queue.add(new SpongePlatformEntityPickupItemEvent(platform, handle, entity));
            }
        }
    }

    //

    private final Entity entity;
    private SpongePlatformEntityPickupItemEvent(
            @NotNull SpongePlatform platform,
            @NotNull ChangeInventoryEvent.Pickup.Pre handle,
            @NotNull Entity entity
    ) {
        super(platform, handle);
        this.entity = entity;
    }

    //

    @Override
    public @NotNull PlatformEntity entity() {
        return this.platform.adapter().entity(this.entity);
    }

}
