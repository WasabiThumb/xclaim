package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlatformEntity extends PlatformObject {

    @Contract(pure = true)
    @NotNull UUID uuid();

    @Contract(pure = true)
    @NotNull PlatformEntityType type();

    @NotNull PlatformPersistentDataContainer pdc();

    @NotNull PlatformLocation location();

    void teleport(@NotNull PlatformLocation location);

}
