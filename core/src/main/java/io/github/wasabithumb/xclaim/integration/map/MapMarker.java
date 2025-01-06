package io.github.wasabithumb.xclaim.integration.map;

import io.github.wasabithumb.xclaim.claim.Claim;
import org.jetbrains.annotations.NotNull;

public interface MapMarker {

    void update(@NotNull Claim claim);

    void deleteMarker();

}
