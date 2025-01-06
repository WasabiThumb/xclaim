package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformPlayerQuitEvent extends BukkitPlatformPlayerEvent<PlayerQuitEvent> implements PlatformPlayerQuitEvent {

    public BukkitPlatformPlayerQuitEvent(@NotNull BukkitPlatform platform, @NotNull PlayerQuitEvent handle) {
        super(platform, handle);
    }

}
