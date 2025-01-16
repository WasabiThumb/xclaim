package io.github.wasabithumb.xclaim.claim.struct;

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

    @ApiStatus.Internal
    public static @NotNull TrustLevel fromOrdinal(int raw) {
        return switch (raw) {
            case 0 -> ALL;
            case 1 -> VETERANS;
            case 2 -> TRUSTED;
            case 3 -> NONE;
            default -> throw new IllegalArgumentException("Invalid trust level ordinal: " + raw);
        };
    }

}
