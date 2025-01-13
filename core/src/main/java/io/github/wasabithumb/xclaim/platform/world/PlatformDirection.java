package io.github.wasabithumb.xclaim.platform.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public enum PlatformDirection {
    NORTH(3),
    EAST(16),
    SOUTH(1),
    WEST(48),
    UP(4),
    DOWN(12),
    NONE(0);

    public static @NotNull PlatformDirection of(int modX, int modY, int modZ) {
        if (modX > 0) return EAST;
        if (modX < 0) return WEST;
        if (modZ > 0) return SOUTH;
        if (modZ < 0) return NORTH;
        if (modY > 0) return NORTH;
        if (modY < 0) return SOUTH;
        return NONE;
    }

    private final int data;
    PlatformDirection(final int data) {
        this.data = data;
    }

    public @Range(from=-1, to=1) int modX() {
        if ((this.data & 16) == 0) return 0;
        return (this.data & 32) == 0 ? 1 : -1;
    }

    public @Range(from=-1, to=1) int modY() {
        if ((this.data & 4) == 0) return 0;
        return (this.data & 8) == 0 ? 1 : -1;
    }

    public @Range(from=-1, to=1) int modZ() {
        if ((this.data & 1) == 0) return 0;
        return (this.data & 2) == 0 ? 1 : -1;
    }

}
