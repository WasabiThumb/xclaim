package io.github.wasabithumb.xclaim.placeholder.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.placeholder.helper.CountInWorldPlaceholder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ClaimCountInWorldPlaceholder extends CountInWorldPlaceholder {

    public ClaimCountInWorldPlaceholder(@NotNull XClaim runtime) {
        super(runtime);
    }

    @Override
    public @NotNull String key() {
        return "claim_count_in";
    }

    @Override
    protected int countFor(@NotNull Claim claim) {
        return 1;
    }

}
