package io.github.wasabithumb.xclaim.platform.scheduler.impl.legacy.task;

import io.github.wasabithumb.xclaim.platform.scheduler.task.BukkitPlatformSchedulerTaskBuilder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class LegacyBukkitPlatformSchedulerTaskBuilder extends BukkitPlatformSchedulerTaskBuilder {

    private final Plugin plugin;
    private final BukkitScheduler handle;
    public LegacyBukkitPlatformSchedulerTaskBuilder(@NotNull Plugin plugin, @NotNull BukkitScheduler handle) {
        this.plugin = plugin;
        this.handle = handle;
    }

    @Override
    public @NotNull LegacyBukkitPlatformSchedulerTask build() {
        Runnable executor = this.assertExecutor();
        BukkitTask bt;
        if (this.isAsync()) {
            if (this.repeats) {
                bt = this.handle.runTaskTimerAsynchronously(
                        this.plugin,
                        executor,
                        this.delay.ticks(),
                        this.period.ticks()
                );
            } else if (this.delay.isInstant()) {
                bt = this.handle.runTaskAsynchronously(this.plugin, executor);
            } else {
                bt = this.handle.runTaskLaterAsynchronously(this.plugin, executor, this.delay.ticks());
            }
        } else {
            if (this.repeats) {
                bt = this.handle.runTaskTimer(
                        this.plugin,
                        executor,
                        this.delay.ticks(),
                        this.period.ticks()
                );
            } else if (this.delay.isInstant()) {
                bt = this.handle.runTask(this.plugin, executor);
            } else {
                bt = this.handle.runTaskLater(this.plugin, executor, this.delay.ticks());
            }
        }
        return new LegacyBukkitPlatformSchedulerTask(bt);
    }

}
