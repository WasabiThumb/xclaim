package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;

public interface PlatformBlockBreakEvent extends PlatformEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.BLOCK_BREAK;

    //

    @NotNull PlatformPlayer player();

    @NotNull PlatformBlock block();

}
