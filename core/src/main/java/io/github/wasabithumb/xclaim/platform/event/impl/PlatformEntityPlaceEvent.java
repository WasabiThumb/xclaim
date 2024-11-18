package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface PlatformEntityPlaceEvent extends PlatformPlayerEvent {

    PlatformEventType TYPE = PlatformEventType.ENTITY_PLACE;

    @NotNull Location location();

    boolean isVehicle();

}
