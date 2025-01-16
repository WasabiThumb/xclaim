package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class SpigotPlatformEvent<E extends Event> extends BukkitPlatformEvent<E> {

    public SpigotPlatformEvent(@NotNull SpigotPlatform platform, @NotNull E handle) {
        super(platform, handle);
    }

    @Override
    protected @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) super.platform();
    }

}
