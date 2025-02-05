package io.github.wasabithumb.xclaim.platform.scheduler.task;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.scheduler.ScheduledTask;

public record SpongePlatformSchedulerTask(
        @NotNull ScheduledTask handle
) implements PlatformSchedulerTask {

    @Override
    public void cancel() {
        this.handle.cancel();
    }

    @Override
    public boolean isCancelled() {
        return handle.isCancelled();
    }

}
