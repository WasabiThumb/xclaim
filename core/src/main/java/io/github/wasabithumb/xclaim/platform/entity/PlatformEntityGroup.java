package io.github.wasabithumb.xclaim.platform.entity;

import org.jetbrains.annotations.NotNull;

public enum PlatformEntityGroup {
    FRIENDLY,
    HOSTILE,
    VEHICLE,
    NOT_ALIVE,
    MISC;

    public boolean contains(@NotNull PlatformEntity entity) {
        return entity.isInGroup(this);
    }
}
