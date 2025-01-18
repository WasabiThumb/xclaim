package io.github.wasabithumb.xclaim.routine;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformSchedulerDuration;
import io.github.wasabithumb.xclaim.platform.scheduler.target.PlatformSchedulerTarget;
import io.github.wasabithumb.xclaim.platform.scheduler.task.PlatformSchedulerTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class Routine {

    protected final XClaim runtime;
    private boolean running;
    private PlatformSchedulerTask tickTask;

    public Routine(@NotNull XClaim runtime) {
        this.runtime = runtime;
        this.running = false;
        this.tickTask = null;
    }

    // Actions

    @ApiStatus.Internal
    public final synchronized void start() {
        if (this.running) return;
        this.onStart();

        if (this.shouldTick()) {
            this.tickTask = this.platform().scheduler().newTask()
                    .target(this.isAsync() ? PlatformSchedulerTarget.async() : PlatformSchedulerTarget.server())
                    .delay(this.delay())
                    .period(this.period())
                    .executor(this::onTick)
                    .build();
        }

        if (this instanceof PlatformListener listener) {
            this.platform().events().register(listener);
        }
        this.running = true;
    }

    @ApiStatus.Internal
    public final synchronized void stop() {
        if (!this.running) return;
        try {
            if (this instanceof PlatformListener listener) {
                this.platform().events().unregister(listener);
            }

            if (this.tickTask != null) {
                if (!this.tickTask.isCancelled())
                    this.tickTask.cancel();
                this.tickTask = null;
            }

            this.onStop();
        } finally {
            this.running = false;
        }
    }

    // Hooks

    protected void onStart() { }

    protected void onStop() { }

    protected void onTick() { }

    // Tick Properties

    protected boolean shouldTick() {
        return false;
    }

    protected boolean isAsync() {
        return false;
    }

    protected @NotNull PlatformSchedulerDuration delay() {
        return PlatformSchedulerDuration.instant();
    }

    protected @NotNull PlatformSchedulerDuration period() {
        return PlatformSchedulerDuration.instant();
    }

    // Helpers

    protected final @NotNull Platform platform() {
        return this.runtime.platform();
    }

}
