package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockMultiPlaceEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockPlaceEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class BuildClaimEnforcer extends ClaimEnforcer.ForPermission {

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

}
