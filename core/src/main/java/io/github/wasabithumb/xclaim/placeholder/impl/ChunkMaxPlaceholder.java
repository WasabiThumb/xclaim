package io.github.wasabithumb.xclaim.placeholder.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.placeholder.AbstractPlaceholder;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ChunkMaxPlaceholder extends AbstractPlaceholder {

    public ChunkMaxPlaceholder(@NotNull XClaim runtime) {
        super(runtime);
    }

    @Override
    public @NotNull String key() {
        return "chunk_max";
    }

    @Override
    public @NotNull String resolve(@NotNull PlatformUser user, @NotNull PlaceholderArgumentQueue args) {
        return Integer.toString(this.rules().maxChunks(user));
    }

}
