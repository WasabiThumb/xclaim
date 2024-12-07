package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitPlatformEntityPlaceEvent extends BukkitPlatformEvent<EntityPlaceEvent> implements PlatformEntityPlaceEvent {

    public BukkitPlatformEntityPlaceEvent(@NotNull BukkitPlatform platform, @NotNull EntityPlaceEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(Objects.requireNonNull(this.handle().getPlayer()));
    }

    @Override
    public @NotNull PlatformLocation location() {
        return this.platform.adapter().location(this.handle.getEntity().getLocation());
    }

    @Override
    public boolean isVehicle() {
        return this.handle.getEntity() instanceof Vehicle;
    }

}
