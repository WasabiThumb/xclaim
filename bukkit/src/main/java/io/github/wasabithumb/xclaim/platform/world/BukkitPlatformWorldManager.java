package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.util.ProxyList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BukkitPlatformWorldManager implements PlatformWorldManager {

    @Override
    public @NotNull List<PlatformWorld> getAll() {
        List<World> backing = Bukkit.getWorlds();
        return new ProxyList<>(
                backing,
                BukkitPlatformWorld::new
        );
    }

    @Override
    public @Nullable BukkitPlatformWorld getWorld(@NotNull UUID uuid) {
        World w = Bukkit.getWorld(uuid);
        if (w == null) return null;
        return new BukkitPlatformWorld(w);
    }

    @Override
    public @Nullable BukkitPlatformWorld getWorld(@NotNull String name) {
        World w = Bukkit.getWorld(name);
        if (w == null) return null;
        return new BukkitPlatformWorld(w);
    }
}
