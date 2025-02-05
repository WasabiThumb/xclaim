package io.github.wasabithumb.xclaim.platform.scheduler.task;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.concurrent.TimeUnit;

public final class SpongePlatformSchedulerTaskBuilder extends AbstractPlatformSchedulerTaskBuilder {

    private final SpongePlatform platform;
    public SpongePlatformSchedulerTaskBuilder(@NotNull SpongePlatform platform) {
        this.platform = platform;
    }

    //

    @Override
    public @NotNull SpongePlatformSchedulerTask build() {
        final boolean async = this.isAsync();
        Task.Builder tb = Task.builder()
                .execute(this.assertExecutor())
                .plugin(this.platform.plugin());

        if (!this.delay.isInstant()) {
            if (async) {
                tb.delay(this.delay.millis(), TimeUnit.MILLISECONDS);
            } else {
                tb.delay(Ticks.of(this.delay.ticks()));
            }
        }

        if (this.repeats) {
            if (async) {
                tb.interval(this.period.millis(), TimeUnit.MILLISECONDS);
            } else if (this.period.isInstant()) {
                tb.interval(Ticks.of(1L));
            } else {
                tb.interval(Ticks.of(this.period.ticks()));
            }
        }

        Task t = tb.build();
        ScheduledTask st = async ?
                Sponge.asyncScheduler().submit(t) :
                this.platform.server().scheduler().submit(t);
        return new SpongePlatformSchedulerTask(st);
    }

}
