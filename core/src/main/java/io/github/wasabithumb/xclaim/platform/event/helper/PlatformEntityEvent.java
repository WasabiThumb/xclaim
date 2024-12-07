package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import org.jetbrains.annotations.NotNull;

public interface PlatformEntityEvent extends PlatformEvent {

    @NotNull PlatformEntity entity();

}
