package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformDirection;
import org.jetbrains.annotations.NotNull;
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

    @NotNull PlatformDirection face();

}
