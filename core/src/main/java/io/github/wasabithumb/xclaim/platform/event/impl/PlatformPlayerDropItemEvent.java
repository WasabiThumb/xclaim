package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;

public interface PlatformPlayerDropItemEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.PLAYER_DROP_ITEM;

}
