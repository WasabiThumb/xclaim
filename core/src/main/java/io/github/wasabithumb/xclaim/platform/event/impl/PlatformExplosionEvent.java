package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlatformExplosionEvent extends PlatformEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.EXPLOSION;

    //

    @Nullable PlatformEntity entity();

    @NotNull List<PlatformBlock> blocks();

}
