package io.github.wasabithumb.xclaim.platform.world;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlatformWorldManager {

    @Nullable World getWorld(@NotNull UUID uuid);

}
