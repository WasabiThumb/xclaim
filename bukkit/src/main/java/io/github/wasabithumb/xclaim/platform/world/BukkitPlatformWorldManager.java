package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.util.ProxyList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BukkitPlatformWorldManager implements PlatformWorldManager {

    private final BukkitPlatform platform;

    @ApiStatus.Internal
    public BukkitPlatformWorldManager(@NotNull BukkitPlatform platform) {
        this.platform = platform;
    }

    //

    @Contract("null -> null; !null -> !null")
    public BukkitPlatformWorld adapt(World world) {
        if (world == null) return null;
        return new BukkitPlatformWorld(this.platform, world);
    }

    @Override
    public @NotNull List<PlatformWorld> getAll() {
        List<World> backing = Bukkit.getWorlds();
        return new ProxyList<>(
                backing,
                this::adapt
        );
    }

    @Override
    public @Nullable BukkitPlatformWorld getWorld(@NotNull UUID uuid) {
        return this.adapt(Bukkit.getWorld(uuid));
    }

    @Override
    public @Nullable BukkitPlatformWorld getWorld(@NotNull String name) {
        return this.adapt(Bukkit.getWorld(name));
    }

}
