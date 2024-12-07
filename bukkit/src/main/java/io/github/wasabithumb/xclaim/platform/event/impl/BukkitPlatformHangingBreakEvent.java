package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlatformHangingBreakEvent extends BukkitPlatformEvent<HangingBreakByEntityEvent> implements PlatformHangingBreakEvent {

    public BukkitPlatformHangingBreakEvent(@NotNull BukkitPlatform platform, @NotNull HangingBreakByEntityEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformEntity getEntity() {
        return this.platform.adapter().entity(this.handle.getEntity());
    }

    @Override
    public @Nullable PlatformEntity getRemover() {
        return this.platform.adapter().entity(this.handle.getRemover());
    }

}
