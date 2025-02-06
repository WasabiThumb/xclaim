package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformPlayerEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.world.BukkitPlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformDirection;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlatformPlayerInteractEvent extends BukkitPlatformPlayerEvent<PlayerInteractEvent> implements PlatformPlayerInteractEvent {

    public BukkitPlatformPlayerInteractEvent(@NotNull BukkitPlatform platform, @NotNull PlayerInteractEvent handle) {
        super(platform, handle);
    }

    protected @NotNull Action action() {
        return this.handle.getAction();
    }

    @Override
    public boolean isPhysical() {
        return this.action().equals(Action.PHYSICAL);
    }

    @Override
    public boolean isLeftClick() {
        return this.action().equals(Action.LEFT_CLICK_AIR) || this.action().equals(Action.LEFT_CLICK_BLOCK);
    }

    @Override
    public boolean isRightClick() {
        return this.action().equals(Action.RIGHT_CLICK_AIR) || this.action().equals(Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public @Nullable PlatformEquipmentSlot getHand() {
        return this.platform.adapter().equipmentSlot(this.handle.getHand());
    }

    @Override
    public @Nullable BukkitPlatformBlock getClickedBlock() {
        return this.platform.adapter().block(this.handle.getClickedBlock());
    }

    @Override
    public @NotNull PlatformDirection face() {
        BlockFace bf = this.handle.getBlockFace();
        return PlatformDirection.of(bf.getModX(), bf.getModY(), bf.getModZ());
    }

}
