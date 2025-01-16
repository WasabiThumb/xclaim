package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import io.github.wasabithumb.xclaim.platform.event.impl.SpigotPlatformChatEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformEventManager extends BukkitPlatformEventManager {

    @ApiStatus.Internal
    public SpigotPlatformEventManager(@NotNull SpigotPlatform platform) {
        super(platform);
    }

    @Override
    protected @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) super.platform();
    }

    @Override
    protected void registerAllImpls() {
        super.registerAllImpls();
        this.registerImpls(
                SpigotPlatformChatEvent.class
        );
    }

}
