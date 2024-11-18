package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import org.jetbrains.annotations.NotNull;

public interface PlatformPlayerEvent extends PlatformEvent {

    @NotNull PlatformPlayer player();

}
