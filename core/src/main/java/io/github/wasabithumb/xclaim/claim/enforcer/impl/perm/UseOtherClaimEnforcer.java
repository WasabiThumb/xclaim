package io.github.wasabithumb.xclaim.claim.enforcer.impl.perm;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class UseOtherClaimEnforcer extends ClaimEnforcer.ForPermission {

    public UseOtherClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.USE_OTHER;
    }

    @PlatformEventHandler
    public void onInteract(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isRightClick()) return;

        PlatformPlayer ply = event.player();
        PlatformBlock block = event.getClickedBlock();
        PlatformLocation location;
        if (block != null) {
            location = block.location();
            if (block.isContainer()) return;
            if (block.isRedstoneComponent()) return;
            if (block.isDoor()) return;
        } else {
            location = ply.location();
        }

        PlatformEquipmentSlot slot = event.getHand();
        if (slot == null) slot = PlatformEquipmentSlot.HAND;
        PlatformItem item = ply.getInventory().getItem(slot);

        if (item != null) {
            PlatformMaterial type = item.type();
            if (NamedPlatformMaterial.WRITTEN_BOOK.equals(type)) {
                return;
            } else if (NamedPlatformMaterial.FIREWORK_ROCKET.equals(type)) {
                if (ply.isGliding()) {
                    if (ply.canBoostElytra()) {
                        ply.boostElytra(item);
                        event.setCancelled(true);
                    }
                    return;
                }
            } else if (item.isConsumable()) {
                return;
            }
        }

        Claim claim = this.claimAt(location);
        if (claim == null) return;
        if (this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

}
