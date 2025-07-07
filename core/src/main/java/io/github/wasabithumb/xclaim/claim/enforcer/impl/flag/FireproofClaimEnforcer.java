package io.github.wasabithumb.xclaim.claim.enforcer.impl.flag;

import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class FireproofClaimEnforcer extends ClaimEnforcer.ForFlag {

    public FireproofClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull ClaimFlag flag() {
        return ClaimFlag.FIREPROOF;
    }

    // TODO

}
