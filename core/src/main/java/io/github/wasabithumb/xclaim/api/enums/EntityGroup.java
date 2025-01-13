package io.github.wasabithumb.xclaim.api.enums;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import org.jetbrains.annotations.NotNull;

public enum EntityGroup {
    FRIENDLY,
    HOSTILE,
    VEHICLE,
    NOT_ALIVE,
    MISC;

    public boolean contains(@NotNull PlatformEntity entity) {
        return entity.isInGroup(this);
    }
}
