package io.github.wasabithumb.xclaim.claim;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ClaimMutationContext(
        @NotNull XClaim runtime,
        @NotNull ClaimManager manager,
        @NotNull Claim claim,
        @NotNull ClaimData data,
        @NotNull PlatformUser user,
        boolean silent
) {

    ClaimMutationContext(@NotNull Claim claim, @NotNull PlatformUser user) {
        this(claim.manager().runtime(), claim.manager(), claim, claim.data(), user, false);
    }

}
