package io.github.wasabithumb.xclaim.platform.scheduler.task;

import io.github.wasabithumb.xclaim.platform.scheduler.PlatformSchedulerDuration;
import io.github.wasabithumb.xclaim.platform.scheduler.target.PlatformSchedulerTarget;
import io.github.wasabithumb.xclaim.platform.scheduler.target.PlatformSchedulerTargetType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class AbstractPlatformSchedulerTaskBuilder implements PlatformSchedulerTaskBuilder {

    protected Runnable executor = null;
    protected PlatformSchedulerTarget target = PlatformSchedulerTarget.server();
    protected PlatformSchedulerDuration delay = PlatformSchedulerDuration.instant();
    protected PlatformSchedulerDuration period = PlatformSchedulerDuration.instant();
    protected boolean repeats = false;

    //

    protected final @NotNull Runnable assertExecutor() {
        Runnable task = this.executor;
        if (task == null) throw new IllegalStateException("Cannot create task without executor");
        return task;
    }

    protected final boolean isAsync() {
        return this.target.type() == PlatformSchedulerTargetType.ASYNC;
    }

    //

    @Override
    public @NotNull PlatformSchedulerTaskBuilder executor(@NotNull Runnable task) {
        this.executor = task;
        return this;
    }

    @Override
    public @NotNull PlatformSchedulerTaskBuilder target(@NotNull PlatformSchedulerTarget target) {
        this.target = target;
        return this;
    }

    @Override
    public @NotNull PlatformSchedulerTaskBuilder delay(@NotNull PlatformSchedulerDuration duration) {
        this.delay = duration;
        return this;
    }

    @Override
    public @NotNull PlatformSchedulerTaskBuilder period(@Nullable PlatformSchedulerDuration duration) {
        if (duration == null) {
            this.repeats = false;
            return this;
        }
        this.period = duration;
        this.repeats = true;
        return this;
    }

}
