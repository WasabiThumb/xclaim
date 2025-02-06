package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface PlatformPlayerInteractEntityEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.PLAYER_INTERACT_ENTITY;

    //

    @UnknownNullability PlatformEquipmentSlot hand();

    @NotNull PlatformEntity interacted();

}
