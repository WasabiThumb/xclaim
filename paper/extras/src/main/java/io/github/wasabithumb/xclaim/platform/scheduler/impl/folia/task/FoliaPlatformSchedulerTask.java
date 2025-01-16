package io.github.wasabithumb.xclaim.platform.scheduler.impl.folia.task;

import io.github.wasabithumb.xclaim.platform.scheduler.task.BukkitPlatformSchedulerTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

public record FoliaPlatformSchedulerTask(
        @NotNull ScheduledTask handle
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
