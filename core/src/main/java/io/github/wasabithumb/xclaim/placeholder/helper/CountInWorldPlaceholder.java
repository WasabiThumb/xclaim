package io.github.wasabithumb.xclaim.placeholder.helper;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.placeholder.AbstractPlaceholder;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class CountInWorldPlaceholder extends AbstractPlaceholder {

    public CountInWorldPlaceholder(@NotNull XClaim runtime) {
        super(runtime);
    }

    @Override
    public @Nullable String resolve(@NotNull PlatformUser user, @NotNull PlaceholderArgumentQueue args) {
        String worldName = args.pollString();
        if (worldName == null) return null;

        PlatformWorld world = this.matchWorld(worldName);
        if (world == null) return "0";

        int count = 0;
        for (Claim claim : this.claims().getByOwner(user)) {
            count += this.countFor(claim);
        }
        return Integer.toString(count);
    }

    protected abstract int countFor(@NotNull Claim claim);

}
