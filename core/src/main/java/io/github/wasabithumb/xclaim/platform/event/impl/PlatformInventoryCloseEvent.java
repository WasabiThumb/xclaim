package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformInventoryEvent;

public interface PlatformInventoryCloseEvent extends PlatformInventoryEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.INVENTORY_CLOSE;

}
