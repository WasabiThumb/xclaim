package io.github.wasabithumb.xclaim.claim.data;

import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public interface ClaimDataManager extends AutoCloseable {

    @NotNull Set<ClaimData.Token> keys();

    @NotNull ClaimData create(@NotNull String name, @NotNull UUID owner, @NotNull PlatformWorld world);

    @Nullable ClaimData load(@NotNull ClaimData.Token key);

    /**
     * Called whenever the claim data has changed. The manager may choose what to do from here; the only guarantee is
     * that all claims are saved by the time #close() executes.
     */
    void queueSync(@NotNull ClaimData data);

    /**
     * Called whenever a claim is deleted. The manager may choose what to do from here; the only guarantee is that the
     * claim is deleted by the time #close() executes.
     */
    void queueDrop(@NotNull ClaimData data);

}
