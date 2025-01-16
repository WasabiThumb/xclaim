package io.github.wasabithumb.xclaim.platform.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface PlatformWorldManager {

    @NotNull List<PlatformWorld> getAll();

    @Nullable PlatformWorld getWorld(@NotNull UUID uuid);

    @Nullable PlatformWorld getWorld(@NotNull String name);

}
