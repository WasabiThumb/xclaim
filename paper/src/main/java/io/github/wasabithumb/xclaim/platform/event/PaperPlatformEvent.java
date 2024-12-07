package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class PaperPlatformEvent<E extends Event> extends BukkitPlatformEvent<E> {

    public PaperPlatformEvent(@NotNull PaperPlatform platform, @NotNull E handle) {
        super(platform, handle);
    }

    @Override
    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) super.platform();
    }

}
