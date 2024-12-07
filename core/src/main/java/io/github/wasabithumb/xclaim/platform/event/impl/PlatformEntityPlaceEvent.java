package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.NotNull;

public interface PlatformEntityPlaceEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ENTITY_PLACE;

    @NotNull PlatformLocation location();

    boolean isVehicle();

}
