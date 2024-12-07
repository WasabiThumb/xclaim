package io.github.wasabithumb.xclaim.platform.scheduler.legacy;

import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformSchedulerTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public record BukkitLegacyPlatformScheduler(
        Plugin plugin,
        BukkitScheduler handle
) implements BukkitPlatformScheduler {

    @Override
    public void synchronize(@NotNull Runnable task) {
        this.handle.runTask(this.plugin, task);
    }

    @Override
    public @NotNull BukkitLegacyPlatformSchedulerTask runTaskTimer(@NotNull Runnable task, long delay, long period) {
        return BukkitPlatformSchedulerTask.legacy(this.handle.runTaskTimer(
                plugin,
                task,
                delay,
                period
        ));
    }

    @Override
    public @NotNull BukkitLegacyPlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Runnable task, long delay, long period) {
        return BukkitPlatformSchedulerTask.legacy(this.handle.runTaskTimerAsynchronously(
                plugin,
                task,
                delay,
                period
        ));
    }

    @Override
    public @NotNull BukkitLegacyPlatformSchedulerTask runTaskAsynchronously(@NotNull Runnable task) {
        return BukkitPlatformSchedulerTask.legacy(this.handle.runTaskAsynchronously(
                plugin,
                task
        ));
    }

}
