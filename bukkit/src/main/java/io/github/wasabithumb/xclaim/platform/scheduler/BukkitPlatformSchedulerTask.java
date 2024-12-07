package io.github.wasabithumb.xclaim.platform.scheduler;

import io.github.wasabithumb.xclaim.platform.scheduler.legacy.BukkitLegacyPlatformSchedulerTask;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlatformSchedulerTask extends PlatformSchedulerTask {

    @Contract("_ -> new")
    static @NotNull BukkitLegacyPlatformSchedulerTask legacy(@NotNull BukkitTask handle) {
        return new BukkitLegacyPlatformSchedulerTask(handle);
    }

}
