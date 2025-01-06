package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformEntityPickupItemEvent extends BukkitPlatformEntityEvent<EntityPickupItemEvent> implements PlatformEntityPickupItemEvent {

    public BukkitPlatformEntityPickupItemEvent(@NotNull BukkitPlatform platform, @NotNull EntityPickupItemEvent handle) {
        super(platform, handle);
    }

}
