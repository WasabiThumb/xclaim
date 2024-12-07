package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformHangingBreakEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.HANGING_BREAK;

    //

    @NotNull PlatformEntity getEntity();

    @Nullable PlatformEntity getRemover();

}
