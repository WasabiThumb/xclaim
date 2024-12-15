package codes.wasabi.xclaim.api.enums;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum TrustLevel {
    ALL,
    VETERANS,
    TRUSTED,
    NONE;

    @ApiStatus.Internal
    public static @NotNull TrustLevel @NotNull [] ascending() {
        return new TrustLevel[] { NONE, TRUSTED, VETERANS, ALL };
    }

    public static TrustLevel fromString(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null; // Handle null or empty strings
        }
        try {
            return TrustLevel.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrustLevel.NONE; // Default fallback value
        }
    }
}
