package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.Nullable;

public interface PlatformInventoryInteractEvent extends PlatformInventoryEvent {

    @Nullable PlatformPlayer player();

}
