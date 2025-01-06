package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformEntityEvent;

public interface PlatformEntityPickupItemEvent extends PlatformEntityEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ENTITY_PICKUP_ITEM;

}
