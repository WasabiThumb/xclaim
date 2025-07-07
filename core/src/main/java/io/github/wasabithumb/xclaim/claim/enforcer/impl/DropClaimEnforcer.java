package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerDropItemEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class DropClaimEnforcer extends ClaimEnforcer.ForPermission {

    public DropClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.ITEM_DROP;
    }

    @PlatformEventHandler
    public void onDrop(@NotNull PlatformPlayerDropItemEvent event) {
        PlatformPlayer player = event.player();
        Claim claim = this.claimAt(player.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, player)) {
            event.setCancelled(true);
        }
    }

}
