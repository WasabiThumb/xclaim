package codes.wasabi.xclaim.platform.spigot;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformScheduler;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformScheduler implements PlatformScheduler {

    private final SpigotPlatform platform;
    private final BukkitScheduler scheduler;
    public SpigotPlatformScheduler(SpigotPlatform platform, BukkitScheduler scheduler) {
        this.platform = platform;
        this.scheduler = scheduler;
    }

    public final BukkitScheduler getHandle() {
        return this.scheduler;
    }

    @Override
    public void synchronize(@NotNull Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            this.scheduler.runTask(XClaim.instance, task);
        }
    }

    @Override
    public @NotNull PlatformSchedulerTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return new SpigotPlatformSchedulerTask(this.platform, this.scheduler.runTaskTimer(plugin, task, delay, period));
    }

    @Override
    public @NotNull PlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period) {
        return new SpigotPlatformSchedulerTask(this.platform, this.scheduler.runTaskTimerAsynchronously(plugin, task, delay, period));
    }

}
