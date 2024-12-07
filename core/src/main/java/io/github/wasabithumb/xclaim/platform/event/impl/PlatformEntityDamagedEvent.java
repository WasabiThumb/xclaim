package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformEntityEvent;
import org.jetbrains.annotations.Nullable;

public interface PlatformEntityDamagedEvent extends PlatformEntityEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ENTITY_DAMAGED;

    //

    @Nullable PlatformEntity getDamager();

    double getDamage();

    void setDamage(double damage);

}
