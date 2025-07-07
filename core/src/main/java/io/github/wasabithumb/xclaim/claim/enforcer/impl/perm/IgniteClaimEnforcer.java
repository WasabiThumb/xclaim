package io.github.wasabithumb.xclaim.claim.enforcer.impl.perm;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEntityEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@ApiStatus.Internal
public final class IgniteClaimEnforcer extends ClaimEnforcer.ForPermission {

    public IgniteClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.FIRE_USE;
    }

    @PlatformEventHandler
    public void onInteract(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isRightClick()) return;
        PlatformBlock block = event.getClickedBlock();
        if (block == null) return;

        PlatformPlayer ply = event.player();
        PlatformItem itemInUse = ply.getInventory()
                .getItem(Objects.requireNonNullElse(event.getHand(), PlatformEquipmentSlot.HAND));

        if (itemInUse == null) return;
        if (!itemInUse.type().ignites()) return;

        Claim claim = this.claimAt(block.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onInteractEntity(@NotNull PlatformPlayerInteractEntityEvent event) {
        PlatformPlayer ply = event.player();
        PlatformItem itemInUse = ply.getInventory()
                .getItem(Objects.requireNonNullElse(event.hand(), PlatformEquipmentSlot.HAND));

        if (itemInUse == null) return;
        if (!itemInUse.type().ignites()) return;

        PlatformEntity entity = event.interacted();
        Claim claim = this.claimAt(entity.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

}
