package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformPlayerEvent<E extends PlayerEvent> extends BukkitPlatformEvent<E> implements PlatformPlayerEvent {

    public BukkitPlatformPlayerEvent(@NotNull BukkitPlatform platform, @NotNull E handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.getPlayer());
    }

}
