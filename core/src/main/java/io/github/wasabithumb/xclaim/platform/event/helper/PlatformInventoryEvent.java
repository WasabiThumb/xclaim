package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import org.jetbrains.annotations.NotNull;

public interface PlatformInventoryEvent extends PlatformEvent {

    @NotNull PlatformInventory inventory();

}
