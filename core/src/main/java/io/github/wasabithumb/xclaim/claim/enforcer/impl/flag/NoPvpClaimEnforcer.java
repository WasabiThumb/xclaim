package io.github.wasabithumb.xclaim.claim.enforcer.impl.flag;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformEntityDamagedEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class NoPvpClaimEnforcer extends ClaimEnforcer.ForFlag {

    public NoPvpClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull ClaimFlag flag() {
        return ClaimFlag.NO_PVP;
    }

    @PlatformEventHandler
    public void onDamage(@NotNull PlatformEntityDamagedEvent event) {
        PlatformEntity target = event.entity();
        if (!(target instanceof PlatformPlayer)) return;

        PlatformEntity attacker = event.damager();
        if (!(attacker instanceof PlatformPlayer attackingPlayer)) return;

        Claim c = this.claimAt(target.location());
        if (c == null) return;

        if (this.isNotPermitted(c, attackingPlayer)) {
            event.setCancelled(true);
        }
    }

}
