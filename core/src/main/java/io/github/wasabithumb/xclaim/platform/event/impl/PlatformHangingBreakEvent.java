package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformHangingBreakEvent extends PlatformEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.HANGING_BREAK;

    //

    @NotNull PlatformEntity entity();

    @Nullable PlatformEntity remover();

}
