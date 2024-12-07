package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PaperPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PaperPlatformEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformItemFrameChangeEvent extends PaperPlatformEvent<PlayerItemFrameChangeEvent> implements PlatformItemFrameChangeEvent {

    public PaperPlatformItemFrameChangeEvent(@NotNull PaperPlatform platform, @NotNull PlayerItemFrameChangeEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PaperPlatformPlayer player() {
        return this.platform().adapter().player(this.handle.getPlayer());
    }

    @Override
    public @NotNull BukkitPlatformEntity itemFrame() {
        return this.platform().adapter().entity(this.handle.getItemFrame());
    }

}
