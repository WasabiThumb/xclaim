package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformInventoryEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformInventoryDragEvent extends BukkitPlatformInventoryEvent<InventoryDragEvent> implements PlatformInventoryDragEvent {

    public BukkitPlatformInventoryDragEvent(@NotNull BukkitPlatform platform, @NotNull InventoryDragEvent handle) {
        super(platform, handle);
    }

}
