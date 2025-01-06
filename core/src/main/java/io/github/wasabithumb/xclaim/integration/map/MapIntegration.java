package io.github.wasabithumb.xclaim.integration.map;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.integration.Integration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MapIntegration extends Integration {

    @Nullable MapMarker getMarker(@NotNull Claim claim);

    default void queueOperation(@NotNull MapOperation op) {
        MapMarker marker = this.getMarker(op.getClaim());
        if (marker != null) op.apply(marker);
    }

}
