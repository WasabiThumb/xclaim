package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.event.impl.PaperPlatformChatEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PaperPlatformItemFrameChangeEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformEventManager extends BukkitPlatformEventManager {

    @ApiStatus.Internal
    public PaperPlatformEventManager(@NotNull PaperPlatform platform) {
        super(platform);
    }

    @Override
    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) super.platform();
    }

    @Override
    protected void registerAllImpls() {
        super.registerAllImpls();
        this.registerImpls(
                PaperPlatformChatEvent.class,
                PaperPlatformItemFrameChangeEvent.class
        );
    }

}
