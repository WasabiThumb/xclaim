package io.github.wasabithumb.xclaim.platform.scheduler;

import io.github.wasabithumb.xclaim.platform.scheduler.legacy.BukkitLegacyPlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlatformScheduler extends PlatformScheduler {

    @Contract("_ -> new")
    static @NotNull BukkitLegacyPlatformScheduler legacy(@NotNull Plugin plugin) {
        return new BukkitLegacyPlatformScheduler(plugin, Bukkit.getScheduler());
    }

}
