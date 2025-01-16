package io.github.wasabithumb.xclaim.platform.scheduler.task;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformSchedulerDuration;
import io.github.wasabithumb.xclaim.platform.scheduler.target.PlatformSchedulerTarget;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformSchedulerTaskBuilder {

    @Contract("_ -> this")
    @NotNull PlatformSchedulerTaskBuilder executor(@NotNull Runnable task);

    @Contract("_ -> this")
    @NotNull PlatformSchedulerTaskBuilder target(@NotNull PlatformSchedulerTarget target);

    @Contract("-> this")
    default @NotNull PlatformSchedulerTaskBuilder async() {
        return this.target(PlatformSchedulerTarget.async());
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder targetEntity(@NotNull PlatformEntity entity) {
        return this.target(PlatformSchedulerTarget.entity(entity));
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder targetChunk(@NotNull PlatformChunk chunk) {
        return this.target(PlatformSchedulerTarget.chunk(chunk));
    }

    @Contract("_ -> this")
    @NotNull PlatformSchedulerTaskBuilder delay(@NotNull PlatformSchedulerDuration duration);

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder delayTicks(long ticks) {
        return this.delay(PlatformSchedulerDuration.ticks(ticks));
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder delayMillis(long millis) {
        return this.delay(PlatformSchedulerDuration.millis(millis));
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder delaySeconds(double seconds) {
        return this.delay(PlatformSchedulerDuration.seconds(seconds));
    }

    @Contract("_ -> this")
    @NotNull PlatformSchedulerTaskBuilder period(@Nullable PlatformSchedulerDuration duration);

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder periodTicks(long ticks) {
        return this.period(PlatformSchedulerDuration.ticks(ticks));
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder periodMillis(long millis) {
        return this.period(PlatformSchedulerDuration.millis(millis));
    }

    @Contract("_ -> this")
    default @NotNull PlatformSchedulerTaskBuilder periodSeconds(double seconds) {
        return this.period(PlatformSchedulerDuration.seconds(seconds));
    }

    @Contract("-> new")
    @NotNull PlatformSchedulerTask build();

}
