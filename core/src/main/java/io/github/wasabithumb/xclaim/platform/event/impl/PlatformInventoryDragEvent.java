package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformInventoryInteractEvent;

public interface PlatformInventoryDragEvent extends PlatformInventoryInteractEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.INVENTORY_DRAG;

}
