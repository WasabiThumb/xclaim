package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformInventory;
import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformInventoryEvent<E extends InventoryEvent> extends BukkitPlatformEvent<E> implements PlatformInventoryEvent {

    public BukkitPlatformInventoryEvent(@NotNull BukkitPlatform platform, @NotNull E handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull BukkitPlatformInventory inventory() {
        return this.platform.adapter().inventory(this.handle.getInventory());
    }

}
