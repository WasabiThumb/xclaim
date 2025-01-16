package io.github.wasabithumb.xclaim.placeholder.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.placeholder.AbstractPlaceholder;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@ApiStatus.Internal
public final class ChunkCountPlaceholder extends AbstractPlaceholder {

    public ChunkCountPlaceholder(@NotNull XClaim runtime) {
        super(runtime);
    }

    @Override
    public @NotNull String key() {
        return "chunk_count";
    }

    @Override
    public @NotNull String resolve(@NotNull PlatformUser user, @NotNull PlaceholderArgumentQueue args) {
        Set<Claim> claims = this.claims().getByOwner(user);
        int count = 0;
        for (Claim c : claims) count += c.chunkCount();
        return Integer.toString(count);
    }

}
