package codes.wasabi.xclaim.map;

import codes.wasabi.xclaim.api.Claim;
import org.jetbrains.annotations.NotNull;

public interface MapMarker {

    void update(@NotNull Claim claim);

    void deleteMarker();

}
