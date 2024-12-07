package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformInventoryEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformInventoryClickEvent extends BukkitPlatformInventoryEvent<InventoryClickEvent> implements PlatformInventoryClickEvent {

    public BukkitPlatformInventoryClickEvent(@NotNull BukkitPlatform platform, @NotNull InventoryClickEvent handle) {
        super(platform, handle);
    }

    @Override
    public int slot() {
        return this.handle.getSlot();
    }

}
