package io.github.wasabithumb.xclaim.platform.scheduler;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.scheduler.task.SpongePlatformSchedulerTaskBuilder;
import org.jetbrains.annotations.NotNull;

public final class SpongePlatformScheduler implements PlatformScheduler {

    private final SpongePlatform platform;

    public SpongePlatformScheduler(@NotNull SpongePlatform platform) {
        this.platform = platform;
    }

    //

    @Override
    public @NotNull SpongePlatformSchedulerTaskBuilder newTask() {
        return new SpongePlatformSchedulerTaskBuilder(this.platform);
    }

}
