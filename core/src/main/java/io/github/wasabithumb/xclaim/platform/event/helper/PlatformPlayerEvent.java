package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.NotNull;

public interface PlatformPlayerEvent extends PlatformEntityEvent {

    @Override
    default @NotNull PlatformEntity entity() {
        return this.player();
    }

    @NotNull PlatformPlayer player();

}
