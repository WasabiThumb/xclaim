package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformPlayerInteractEntityEvent extends BukkitPlatformPlayerEvent<PlayerInteractEntityEvent> implements PlatformPlayerInteractEntityEvent {

    public BukkitPlatformPlayerInteractEntityEvent(@NotNull BukkitPlatform platform, @NotNull PlayerInteractEntityEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformEquipmentSlot hand() {
        return this.platform.adapter().equipmentSlot(this.handle.getHand());
    }

    @Override
    public @NotNull BukkitPlatformEntity interacted() {
        return this.platform.adapter().entity(this.handle.getRightClicked());
    }

}
