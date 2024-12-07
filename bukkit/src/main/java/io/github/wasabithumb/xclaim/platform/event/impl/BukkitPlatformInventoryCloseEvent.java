package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformInventoryEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformInventoryCloseEvent extends BukkitPlatformInventoryEvent<InventoryCloseEvent> implements PlatformInventoryCloseEvent {

    public BukkitPlatformInventoryCloseEvent(@NotNull BukkitPlatform platform, @NotNull InventoryCloseEvent handle) {
        super(platform, handle);
    }

}
