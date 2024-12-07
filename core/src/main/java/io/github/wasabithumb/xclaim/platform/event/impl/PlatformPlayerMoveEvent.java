package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface PlatformPlayerMoveEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.PLAYER_MOVE;

    //

    @UnknownNullability PlatformLocation getFrom();

    @UnknownNullability PlatformLocation getTo();

    void setFrom(@NotNull PlatformLocation location);

    void setTo(@NotNull PlatformLocation location);

}
