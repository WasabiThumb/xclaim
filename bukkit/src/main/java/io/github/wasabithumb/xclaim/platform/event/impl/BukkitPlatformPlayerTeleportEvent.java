package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformPlayerTeleportEvent extends BukkitPlatformPlayerEvent<PlayerTeleportEvent> implements PlatformPlayerTeleportEvent {

    public BukkitPlatformPlayerTeleportEvent(@NotNull BukkitPlatform platform, @NotNull PlayerTeleportEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformLocation destination() {
        Location loc = this.handle.getTo();
        if (loc == null) loc = this.handle.getPlayer().getLocation();
        return this.platform.adapter().location(loc);
    }

}
