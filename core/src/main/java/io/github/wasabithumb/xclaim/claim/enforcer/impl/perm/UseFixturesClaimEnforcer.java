package io.github.wasabithumb.xclaim.claim.enforcer.impl.perm;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntityGroup;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformItemFrameChangeEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEntityEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerInteractEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class UseFixturesClaimEnforcer extends ClaimEnforcer.ForPermission {

    public UseFixturesClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.USE_FIXTURES;
    }

    @PlatformEventHandler
    public void onInteractEntity(@NotNull PlatformPlayerInteractEntityEvent event) {
        PlatformEntity ent = event.entity();
        if (!ent.isInGroup(PlatformEntityGroup.NOT_ALIVE)) return;

        Claim claim = this.claimAt(ent.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    @PlatformEventHandler
    public void onChangeItemFrame(@NotNull PlatformItemFrameChangeEvent event) {
        Claim claim = this.claimAt(event.itemFrame().location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

}
