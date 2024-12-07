package io.github.wasabithumb.xclaim.platform.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public record FoliaPlatformScheduler(
        @NotNull Plugin plugin,
        @NotNull FoliaPlatformSchedulerReflection reflection
) implements BukkitPlatformScheduler {

    @Override
    public void synchronize(@NotNull Runnable task) {
        this.reflection.use((r) -> {
            r.mGlobalSchedulerRun().invoke(
                    r.oGlobalScheduler(),
                    this.plugin,
                    this.consumer(task)
            );
            return null;
        });
    }

    @Override
    public @NotNull FoliaPlatformSchedulerTask runTaskTimer(@NotNull Runnable task, long delay, long period) {
        return this.task(this.reflection.use((r) -> r.mGlobalSchedulerRunAtFixedRate().invoke(
                r.oGlobalScheduler(),
                this.plugin,
                this.consumer(task),
                delay,
                period
        )));
    }

    @Override
    public @NotNull FoliaPlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Runnable task, long delay, long period) {
        return this.task(this.reflection.use((r) -> r.mAsyncSchedulerRunAtFixedRate().invoke(
                r.oAsyncScheduler(),
                this.plugin,
                this.consumer(task),
                this.ticksToMs(delay),
                this.ticksToMs(period),
                TimeUnit.MILLISECONDS
        )));
    }

    @Override
    public @NotNull FoliaPlatformSchedulerTask runTaskAsynchronously(@NotNull Runnable task) {
        return this.task(this.reflection.use((r) -> r.mAsyncSchedulerRunNow().invoke(
                r.oAsyncScheduler(),
                this.plugin,
                this.consumer(task)
        )));
    }

    private @NotNull FoliaPlatformSchedulerTask task(@NotNull Object handle) {
        return new FoliaPlatformSchedulerTask(handle, this.reflection);
    }

    private @NotNull Consumer<?> consumer(@NotNull Runnable task) {
        return (Consumer<Object>) o -> task.run();
    }

    private long ticksToMs(long ticks) {
        return Math.floorDiv(ticks * 1000L, 20L);
    }

}
