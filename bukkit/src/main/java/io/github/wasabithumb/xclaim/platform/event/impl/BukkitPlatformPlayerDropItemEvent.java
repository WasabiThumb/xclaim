package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformPlayerDropItemEvent extends BukkitPlatformPlayerEvent<PlayerDropItemEvent> implements PlatformPlayerDropItemEvent {

    public BukkitPlatformPlayerDropItemEvent(@NotNull BukkitPlatform platform, @NotNull PlayerDropItemEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformEntity getDrop() {
        return this.platform().adapter().entity(this.handle.getItemDrop());
    }

}
