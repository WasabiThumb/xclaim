package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import org.jetbrains.annotations.NotNull;

public interface PlatformItemFrameChangeEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ITEM_FRAME_CHANGE;

    //

    @NotNull PlatformEntity itemFrame();

}
