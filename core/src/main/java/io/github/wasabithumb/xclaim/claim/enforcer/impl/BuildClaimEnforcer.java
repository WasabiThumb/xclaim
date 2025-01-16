package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockMultiPlaceEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockPlaceEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class BuildClaimEnforcer extends ClaimEnforcer {

    public BuildClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.BUILD;
    }

    @PlatformEventHandler
    public void onPlace(@NotNull PlatformBlockPlaceEvent event) {
        PlatformBlock block = event.block();
        Claim claim = this.claimAt(block.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onPlace(@NotNull PlatformBlockMultiPlaceEvent event) {
        for (PlatformBlock block : event.blocks()) {
            Claim claim = this.claimAt(block.location());
            if (claim == null) continue;
            if (this.isNotPermitted(claim, event.player())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @PlatformEventHandler
    public void onPlaceFluid(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isRightClick()) return;
        PlatformBlock block = event.getClickedBlock();
        if (block == null) return;

        PlatformEquipmentSlot hand = event.getHand();
        if (hand == null) hand = PlatformEquipmentSlot.HAND;

        PlatformPlayer ply = event.player();
        PlatformItem item = ply.getInventory().getItem(hand);
        if (item == null) return;

        if (!item.type().isBucket()) return;
        if (item.type() == NamedPlatformMaterial.BUCKET) return;

        PlatformBlock target = block;
        if (!block.canWaterlog()) {
            target = block.relative(event.face());
        }

        Claim claim = this.claimAt(target.location());
        if (claim == null) return;

        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

}
