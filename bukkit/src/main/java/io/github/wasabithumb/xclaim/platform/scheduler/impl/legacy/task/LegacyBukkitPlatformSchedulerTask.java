package io.github.wasabithumb.xclaim.platform.scheduler.impl.legacy.task;

import io.github.wasabithumb.xclaim.platform.scheduler.task.BukkitPlatformSchedulerTask;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public record LegacyBukkitPlatformSchedulerTask(
        @NotNull BukkitTask handle
) implements BukkitPlatformSchedulerTask {

    @Override
    public void cancel() {
        this.handle.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.handle.isCancelled();
    }

}
