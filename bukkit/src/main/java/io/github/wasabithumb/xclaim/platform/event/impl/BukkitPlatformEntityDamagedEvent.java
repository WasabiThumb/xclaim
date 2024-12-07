package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlatformEntityDamagedEvent extends BukkitPlatformEntityEvent<EntityDamageEvent> implements PlatformEntityDamagedEvent {

    public BukkitPlatformEntityDamagedEvent(@NotNull BukkitPlatform platform, @NotNull EntityDamageEvent handle) {
        super(platform, handle);
    }

    @Override
    public @Nullable PlatformEntity getDamager() {
        if (this.handle instanceof EntityDamageByEntityEvent byEntity) {
            return this.platform.adapter().entity(byEntity.getDamager());
        }
        return null;
    }

    @Override
    public double getDamage() {
        return this.handle.getDamage();
    }

    @Override
    public void setDamage(double damage) {
        this.handle.setDamage(damage);
    }

}
