package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class BukkitPlatformPlayerMoveEvent extends BukkitPlatformPlayerEvent<PlayerMoveEvent> implements PlatformPlayerMoveEvent {

    public BukkitPlatformPlayerMoveEvent(@NotNull BukkitPlatform platform, @NotNull PlayerMoveEvent handle) {
        super(platform, handle);
    }

    @Override
    public @UnknownNullability PlatformLocation getFrom() {
        return this.platform.adapter().location(this.handle.getFrom());
    }

    @Override
    public @UnknownNullability PlatformLocation getTo() {
        return this.platform.adapter().location(this.handle.getTo());
    }

    @Override
    public void setFrom(@NotNull PlatformLocation location) {
        this.handle.setFrom(this.platform.adapter().location(location));
    }

    @Override
    public void setTo(@NotNull PlatformLocation location) {
        this.handle.setTo(this.platform.adapter().location(location));
    }

}
