package io.github.wasabithumb.xclaim.platform.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlatformWorldManager {

    @Nullable PlatformWorld getWorld(@NotNull UUID uuid);

    @Nullable PlatformWorld getWorld(@NotNull String name);

}
