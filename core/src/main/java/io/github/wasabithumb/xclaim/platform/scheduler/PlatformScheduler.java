package io.github.wasabithumb.xclaim.platform.scheduler;

import io.github.wasabithumb.xclaim.platform.scheduler.task.PlatformSchedulerTaskBuilder;
import org.jetbrains.annotations.NotNull;

public interface PlatformScheduler {

    @NotNull PlatformSchedulerTaskBuilder newTask();

    default void synchronize(@NotNull Runnable task) {
        this.newTask().executor(task).build();
    }

}
