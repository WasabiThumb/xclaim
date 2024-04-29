package codes.wasabi.xclaim.platform.folia_1_19;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformScheduler;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class FoliaPlatformScheduler implements PlatformScheduler {

    private final AsyncScheduler asyncScheduler;
    private final GlobalRegionScheduler globalRegionScheduler;
    public FoliaPlatformScheduler(AsyncScheduler asyncScheduler, GlobalRegionScheduler globalRegionScheduler) {
        this.asyncScheduler = asyncScheduler;
        this.globalRegionScheduler = globalRegionScheduler;
    }

    public final AsyncScheduler getAsyncScheduler() {
        return this.asyncScheduler;
    }

    public final GlobalRegionScheduler getGlobalRegionScheduler() {
        return this.globalRegionScheduler;
    }

    @Override
    public void synchronize(@NotNull Runnable task) {
        this.globalRegionScheduler.execute(XClaim.instance, task);
    }

    @Override
    public @NotNull PlatformSchedulerTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        ScheduledTask ref = this.globalRegionScheduler.runAtFixedRate(plugin, (ScheduledTask t) -> {
            task.run();
        }, Math.max(delay, 1L), Math.max(period, 1L));
        return new FoliaPlatformSchedulerTask(ref);
    }

    @Override
    public @NotNull PlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        long delayMillis = Math.max(Math.round((((double) delay) / 20d) * 1000d), 1L);
        long periodMillis = Math.max(Math.round((((double) period) / 20d) * 1000d), 1L);
        ScheduledTask ref = this.asyncScheduler.runAtFixedRate(plugin, (ScheduledTask t) -> {
            task.run();
        }, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
        return new FoliaPlatformSchedulerTask(ref);
    }

    @Override
    public @NotNull PlatformSchedulerTask runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task) {
        ScheduledTask ref = this.asyncScheduler.runNow(plugin, (ScheduledTask t) -> {
            task.run();
        });
        return new FoliaPlatformSchedulerTask(ref);
    }

}
