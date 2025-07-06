package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.*;

import java.util.Objects;

@ApiStatus.Internal
public final class InteractClaimEnforcer extends ClaimEnforcer {

    public InteractClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.INTERACT;
    }

    //

    @PlatformEventHandler
    public void onInteract(@NotNull PlatformPlayerInteractEvent event) {
        PlatformPlayer ply = event.player();
        if (event.isRightClick()) {
            PlatformItem itemInUse = ply.getInventory()
                    .getItem(Objects.requireNonNullElse(event.getHand(), PlatformEquipmentSlot.HAND));
            switch (this.checkSpecialItem(ply, itemInUse)) {
                case 1:
                    return;
                case 2:
                    event.setCancelled(true);
                    return;
            }
        }

        PlatformLocation location = ply.location();
        PlatformBlock block = event.getClickedBlock();
        if (block != null) location = block.location();

        Claim claim = this.claimAt(location);
        if (claim == null) return;
        if (this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

    private @Range(from=0L, to=2L) int checkSpecialItem(
            @NotNull PlatformPlayer ply,
            @Nullable PlatformItem item
    ) {
        if (item == null) return 0;
        PlatformMaterial type = item.type();

        if (type.equals(NamedPlatformMaterial.WRITTEN_BOOK)) {
            ply.openBook(item);
            return 2;
        }

        if (type.equals(NamedPlatformMaterial.FIREWORK_ROCKET)) {
            if (ply.isGliding()) {
                if (ply.canBoostElytra()) {
                    ply.boostElytra(item);
                    return 2;
                } else {
                    return 1;
                }
            }
            return 0;
        }

        if (item.isConsumable()) {
            return 1;
        }

        return 0;
    }

}
