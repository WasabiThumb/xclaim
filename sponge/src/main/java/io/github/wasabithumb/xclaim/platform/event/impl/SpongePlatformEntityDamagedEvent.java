package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.SpongePlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class SpongePlatformEntityDamagedEvent
        extends SpongePlatformEvent<DamageEntityEvent>
        implements PlatformEntityDamagedEvent
{

    @Adapter
    public SpongePlatformEntityDamagedEvent(
            @NotNull SpongePlatform platform,
            @NotNull DamageEntityEvent handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @Nullable PlatformEntity damager() {
        SpongePlatformTypeAdapter adapter = this.platform.adapter();
        return this.handle.cause()
                .first(Entity.class)
                .map(adapter::entity)
                .orElse(null);
    }

    @Override
    public double getDamage() {
        return this.handle.baseDamage();
    }

    @Override
    public void setDamage(double damage) {
        this.handle.setBaseDamage(damage);
    }

    @Override
    public @NotNull PlatformEntity entity() {
        return this.platform.adapter().entity(this.handle.entity());
    }

}
