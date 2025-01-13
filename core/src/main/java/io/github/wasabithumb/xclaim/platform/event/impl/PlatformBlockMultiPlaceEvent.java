package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PlatformBlockMultiPlaceEvent extends PlatformEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.BLOCK_MULTI_PLACE;

    //

    @NotNull PlatformPlayer player();

    @NotNull Collection<PlatformBlock> blocks();

}
