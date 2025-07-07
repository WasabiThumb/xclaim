package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockFlowEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class FluidClaimEnforcer extends ClaimEnforcer.ForPermission {

    public FluidClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    //

    @Override
    protected @NotNull Permission permission() {
        return Permission.BUILD;
    }

    //

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

        PlatformBlock target = block.canWaterlog() ? block : block.relative(event.face());

        Claim claim = this.claimAt(target.location());
        if (claim != null && this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
            return;
        }

        this.setFluidOwner(target, ply.uuid());
    }

    @PlatformEventHandler
    public void onFlow(@NotNull PlatformBlockFlowEvent event) {
        PlatformBlock source = event.block();
        PlatformBlock target = event.targetBlock();

        UUID owner = this.getFluidOwner(source);
        boolean noOwner = owner == null;

        Claim claim = this.claimAt(target.location());
        if (claim != null) {
            boolean allowed = noOwner ?
                    claim.getGlobalPermission(Permission.BUILD) == TrustLevel.ALL :
                    claim.checkPermission(this.platform().users().getUser(owner), Permission.BUILD);

            if (!allowed) {
                event.setCancelled(true);
                return;
            }
        }

        if (noOwner) return;
        this.setFluidOwner(target, owner);
    }

    private @Nullable UUID getFluidOwner(@NotNull PlatformBlock block) {
        // TODO: Implement
        return null;
    }

    private void setFluidOwner(@NotNull PlatformBlock block, @NotNull UUID owner) {
        // TODO: Implement
    }

}
