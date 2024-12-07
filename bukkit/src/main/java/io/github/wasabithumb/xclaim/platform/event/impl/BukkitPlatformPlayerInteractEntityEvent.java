package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class BukkitPlatformPlayerInteractEntityEvent extends BukkitPlatformPlayerEvent<PlayerInteractEntityEvent> implements PlatformPlayerInteractEntityEvent {

    public BukkitPlatformPlayerInteractEntityEvent(@NotNull BukkitPlatform platform, @NotNull PlayerInteractEntityEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull BukkitPlatformEntity getRightClicked() {
        return this.platform.adapter().entity(this.handle.getRightClicked());
    }

    @Override
    public boolean hasInteractionPoint() {
        return this.handle instanceof PlayerInteractAtEntityEvent;
    }

    @Override
    public @UnknownNullability PlatformLocation getInteractionPoint() {
        if (this.handle instanceof PlayerInteractAtEntityEvent atEntity) {
            return this.platform.adapter().location(
                    atEntity
                            .getClickedPosition()
                            .toLocation(this.handle.getRightClicked().getWorld())
            );
        }
        return null;
    }

}
