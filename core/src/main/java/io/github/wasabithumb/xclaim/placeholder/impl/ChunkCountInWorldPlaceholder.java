package io.github.wasabithumb.xclaim.placeholder.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.placeholder.helper.CountInWorldPlaceholder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ChunkCountInWorldPlaceholder extends CountInWorldPlaceholder {

    public ChunkCountInWorldPlaceholder(@NotNull XClaim runtime) {
        super(runtime);
    }

    @Override
    public @NotNull String key() {
        return "chunk_count_in";
    }

    @Override
    protected int countFor(@NotNull Claim claim) {
        return claim.chunkCount();
    }

}
