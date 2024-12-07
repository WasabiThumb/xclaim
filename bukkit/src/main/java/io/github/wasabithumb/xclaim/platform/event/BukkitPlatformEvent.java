package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformEvent<E extends Event> implements PlatformEvent {

    protected final BukkitPlatform platform;
    protected final E handle;
    public BukkitPlatformEvent(@NotNull BukkitPlatform platform, @NotNull E handle) {
        this.platform = platform;
        this.handle = handle;
    }

    protected @NotNull BukkitPlatform platform() {
        return this.platform;
    }

    @Override
    public @NotNull E handle() {
        return this.handle;
    }

    @Override
    public boolean isCancelled() {
        return this.handle instanceof Cancellable c && c.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if (this.handle instanceof Cancellable c)
            c.setCancelled(cancelled);
    }

}
