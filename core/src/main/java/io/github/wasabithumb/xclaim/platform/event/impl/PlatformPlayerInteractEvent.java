package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.Nullable;

public interface PlatformPlayerInteractEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.PLAYER_INTERACT;

    //

    boolean isPhysical();

    boolean isLeftClick();

    boolean isRightClick();

    boolean isPlacement();

    @Nullable PlatformEquipmentSlot getHand();

    @Nullable PlatformBlock getClickedBlock();

}
