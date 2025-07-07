package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.NamedPlatformEntityType;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockBreakEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformEntityDamagedEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformHangingBreakEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class BreakClaimEnforcer extends ClaimEnforcer.ForPermission {

    public BreakClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.BREAK;
    }

    @PlatformEventHandler
    public void onBreak(@NotNull PlatformBlockBreakEvent event) {
        Claim claim = this.claimAt(event.block().location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onBreakHanging(@NotNull PlatformHangingBreakEvent event) {
        Claim claim = this.claimAt(event.entity().location());
        if (claim == null) return;

        PlatformEntity remover = event.remover();
        if (remover instanceof PlatformPlayer ply && this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onPickupFluid(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isRightClick()) return;
        PlatformBlock block = event.getClickedBlock();
        if (block == null) return;

        PlatformEquipmentSlot hand = event.getHand();
        if (hand == null) hand = PlatformEquipmentSlot.HAND;

        PlatformPlayer ply = event.player();
        PlatformItem item = ply.getInventory().getItem(hand);
        if (item == null) return;
        if (item.type() != NamedPlatformMaterial.BUCKET) return;

        PlatformBlock target = block;
        if (!block.isWaterlogged()) {
            target = block.relative(event.face());
        }

        Claim claim = this.claimAt(target.location());
        if (claim == null) return;

        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onTrample(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isPhysical()) return;

        PlatformBlock block = event.getClickedBlock();
        if (block == null) return;
        if (!block.getType().isSoil()) return;

        Claim claim = this.claimAt(block.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onBreakEndCrystal(@NotNull PlatformEntityDamagedEvent event) {
        PlatformEntity entity = event.entity();
        if (!entity.type().equals(NamedPlatformEntityType.END_CRYSTAL)) return;

        Claim claim = this.claimAt(entity.location());
        if (claim == null) return;

        if (event.damager() instanceof PlatformPlayer ply && this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

}
