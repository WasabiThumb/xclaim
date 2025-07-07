package io.github.wasabithumb.xclaim.claim.enforcer.impl.flag;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformBlockFlowEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class WaterproofClaimEnforcer extends ClaimEnforcer.ForFlag {

    public WaterproofClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull ClaimFlag flag() {
        return ClaimFlag.WATERPROOF;
    }

    @PlatformEventHandler
    public void onFlow(@NotNull PlatformBlockFlowEvent event) {
        Claim c = this.claimAt(event.targetBlock().location());
        if (c == null || !c.getFlags().contains(this.flag())) return;

        PlatformBlock origin = event.block();
        if (c.containsChunk(origin.x() >> 4, origin.z() >> 4)) return;

        event.setCancelled(true);
    }

}
