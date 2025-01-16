package io.github.wasabithumb.xclaim.platform.scheduler.task;

import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformSchedulerTaskBuilder extends AbstractPlatformSchedulerTaskBuilder {

    @Override
    public abstract @NotNull BukkitPlatformSchedulerTask build();

}
