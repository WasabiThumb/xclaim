package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformEntityEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlatformEntityExplodeEvent extends PlatformEntityEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ENTITY_EXPLODE;

    //

    @NotNull List<PlatformBlock> blocks();

}
