package io.github.wasabithumb.xclaim.platform.scheduler;

import org.jetbrains.annotations.NotNull;

public interface PlatformScheduler {

    void synchronize(@NotNull Runnable task);

    @NotNull PlatformSchedulerTask runTaskTimer(@NotNull Runnable task, long delay, long period);

    @NotNull PlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Runnable task, long delay, long period);

    @NotNull PlatformSchedulerTask runTaskAsynchronously(@NotNull Runnable task);

}
