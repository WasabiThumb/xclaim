package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformPlayerJoinEvent extends BukkitPlatformPlayerEvent<PlayerJoinEvent> implements PlatformPlayerJoinEvent {

    public BukkitPlatformPlayerJoinEvent(@NotNull BukkitPlatform platform, @NotNull PlayerJoinEvent handle) {
        super(platform, handle);
    }

}
