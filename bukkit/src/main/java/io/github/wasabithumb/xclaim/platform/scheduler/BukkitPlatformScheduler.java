package io.github.wasabithumb.xclaim.platform.scheduler;

import io.github.wasabithumb.xclaim.platform.scheduler.impl.legacy.LegacyBukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.task.BukkitPlatformSchedulerTaskBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlatformScheduler extends PlatformScheduler {

    @Contract("_ -> new")
    static @NotNull LegacyBukkitPlatformScheduler legacy(@NotNull Plugin plugin) {
        return new LegacyBukkitPlatformScheduler(plugin, Bukkit.getScheduler());
    }

    //

    @Override
    @NotNull BukkitPlatformSchedulerTaskBuilder newTask();

}
