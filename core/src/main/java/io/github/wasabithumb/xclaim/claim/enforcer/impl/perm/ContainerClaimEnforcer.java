package io.github.wasabithumb.xclaim.claim.enforcer.impl.perm;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ContainerClaimEnforcer extends ClaimEnforcer.ForPermission {

    public ContainerClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.CHEST_OPEN;
    }

    @PlatformEventHandler
    public void onInteract(@NotNull PlatformPlayerInteractEvent event) {
        if (!event.isRightClick()) return;
        PlatformBlock block = event.getClickedBlock();
        if (block == null) return;
        if (!block.isContainer()) return;

        Claim claim = this.claimAt(block.location());
        if (claim == null) return;

        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

}
