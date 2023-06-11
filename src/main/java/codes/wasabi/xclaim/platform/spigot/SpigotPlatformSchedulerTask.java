package codes.wasabi.xclaim.platform.spigot;

import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotPlatformSchedulerTask implements PlatformSchedulerTask {

    private final SpigotPlatform platform;
    private final BukkitTask task;
    public SpigotPlatformSchedulerTask(SpigotPlatform platform, BukkitTask task) {
        this.platform = platform;
        this.task = task;
    }

    public final BukkitTask getHandle() {
        return this.task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return platform.bukkitTaskCancelled(this.task);
    }

}
