package io.github.wasabithumb.xclaim.platform.scheduler.impl.folia.task;

import io.github.wasabithumb.xclaim.platform.BukkitPlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.scheduler.impl.folia.FoliaPlatformSchedulerFacets;
import io.github.wasabithumb.xclaim.platform.scheduler.target.PlatformSchedulerTarget;
import io.github.wasabithumb.xclaim.platform.scheduler.task.BukkitPlatformSchedulerTaskBuilder;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaPlatformSchedulerTaskBuilder extends BukkitPlatformSchedulerTaskBuilder {

    private final Plugin plugin;
    private final BukkitPlatformTypeAdapter adapter;
    private final FoliaPlatformSchedulerFacets facets;

    public FoliaPlatformSchedulerTaskBuilder(
            @NotNull Plugin plugin,
            @NotNull BukkitPlatformTypeAdapter adapter,
            @NotNull FoliaPlatformSchedulerFacets facets
    ) {
        this.plugin = plugin;
        this.adapter = adapter;
        this.facets = facets;
    }

    @Override
    public @NotNull FoliaPlatformSchedulerTask build() {
        ExecutorWrapper executor = new ExecutorWrapper(this.assertExecutor());
        PlatformSchedulerTarget target = this.target;

        ScheduledTask st = switch (target.type()) {
            case SERVER -> this.buildServer(executor);
            case ASYNC -> this.buildAsync(executor);
            case CHUNK -> this.buildChunk(executor, target.chunk());
            case ENTITY -> this.buildEntity(executor, target.entity());
        };

        return new FoliaPlatformSchedulerTask(st);
    }

    private @NotNull ScheduledTask buildServer(@NotNull ExecutorWrapper executor) {
        if (this.repeats) {
            return this.facets.global().runAtFixedRate(
                    this.plugin,
                    executor,
                    this.delay.isInstant() ? 1L : this.delay.ticks(),
                    this.period.isInstant() ? 1L : this.period.ticks()
            );
        } else if (this.delay.isInstant()) {
            return this.facets.global().run(
                    this.plugin,
                    executor
            );
        } else {
            return this.facets.global().runDelayed(
                    this.plugin,
                    executor,
                    this.delay.isInstant() ? 1L : this.delay.ticks()
            );
        }
    }

    private @NotNull ScheduledTask buildAsync(@NotNull ExecutorWrapper executor) {
        if (this.repeats) {
            return this.facets.async().runAtFixedRate(
                    this.plugin,
                    executor,
                    this.delay.millis(),
                    this.period.isInstant() ? 50L : this.period.millis(),
                    TimeUnit.MILLISECONDS
            );
        } else if (this.delay.isInstant()) {
            return this.facets.async().runNow(
                    this.plugin,
                    executor
            );
        } else {
            return this.facets.async().runDelayed(
                    this.plugin,
                    executor,
                    this.delay.millis(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    private @NotNull ScheduledTask buildChunk(@NotNull ExecutorWrapper executor, @NotNull PlatformChunk chunk) {
        Chunk bc = this.adapter.chunk(chunk);
        if (this.repeats) {
            return this.facets.region().runAtFixedRate(
                    this.plugin,
                    bc.getWorld(),
                    bc.getX(),
                    bc.getZ(),
                    executor,
                    this.delay.isInstant() ? 1L : this.delay.ticks(),
                    this.period.isInstant() ? 1L : this.period.ticks()
            );
        } else if (this.delay.isInstant()) {
            return this.facets.region().run(
                    this.plugin,
                    bc.getWorld(),
                    bc.getX(),
                    bc.getZ(),
                    executor
            );
        } else {
            return this.facets.region().runDelayed(
                    this.plugin,
                    bc.getWorld(),
                    bc.getX(),
                    bc.getZ(),
                    executor,
                    this.delay.isInstant() ? 1L : this.delay.ticks()
            );
        }
    }

    private @NotNull ScheduledTask buildEntity(@NotNull ExecutorWrapper executor, @NotNull PlatformEntity entity) {
        Entity be = this.adapter.entity(entity);
        ScheduledTask st;
        if (this.repeats) {
            st = this.facets.entity(be).runAtFixedRate(
                    this.plugin,
                    executor,
                    null,
                    this.delay.isInstant() ? 1L : this.delay.ticks(),
                    this.period.isInstant() ? 1L : this.period.ticks()
            );
        } else if (this.delay.isInstant()) {
            st = this.facets.entity(be).run(
                    this.plugin,
                    executor,
                    null
            );
        } else {
            st = this.facets.entity(be).runDelayed(
                    this.plugin,
                    executor,
                    null,
                    this.delay.isInstant() ? 1L : this.delay.ticks()
            );
        }
        if (st == null)
            return this.buildChunk(executor, entity.location().chunk());
        return st;
    }

    //

    private record ExecutorWrapper(
            @NotNull Runnable executor
    ) implements Consumer<ScheduledTask> {

        @Override
        public void accept(ScheduledTask ignored) {
            this.executor.run();
        }

    }

}
