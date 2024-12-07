package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformEntityEvent<E extends EntityEvent> extends BukkitPlatformEvent<E> implements PlatformEntityEvent {

    public BukkitPlatformEntityEvent(@NotNull BukkitPlatform platform, @NotNull E handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull BukkitPlatformEntity entity() {
        return this.platform.adapter().entity(this.handle.getEntity());
    }

}
