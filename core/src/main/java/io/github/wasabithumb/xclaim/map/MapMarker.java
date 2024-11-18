package io.github.wasabithumb.xclaim.map;

import io.github.wasabithumb.xclaim.api.Claim;
import org.jetbrains.annotations.NotNull;

public interface MapMarker {

    void update(@NotNull Claim claim);

    void deleteMarker();

}
