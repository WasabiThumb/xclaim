package io.github.wasabithumb.xclaim.platform.scheduler.legacy;

import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformSchedulerTask;
import org.bukkit.scheduler.BukkitTask;

public record BukkitLegacyPlatformSchedulerTask(
        BukkitTask handle
) implements BukkitPlatformSchedulerTask {

    @Override
    public void cancel() {
        this.handle.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.handle.isCancelled();
    }

}
