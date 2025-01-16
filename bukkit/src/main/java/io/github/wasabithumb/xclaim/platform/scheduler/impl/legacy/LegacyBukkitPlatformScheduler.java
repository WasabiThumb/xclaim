package io.github.wasabithumb.xclaim.platform.scheduler.impl.legacy;

import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.impl.legacy.task.LegacyBukkitPlatformSchedulerTaskBuilder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class LegacyBukkitPlatformScheduler implements BukkitPlatformScheduler {

    private final Plugin plugin;
    private final BukkitScheduler handle;
    public LegacyBukkitPlatformScheduler(@NotNull Plugin plugin, @NotNull BukkitScheduler handle) {
        this.plugin = plugin;
        this.handle = handle;
    }

    @Override
    public @NotNull LegacyBukkitPlatformSchedulerTaskBuilder newTask() {
        return new LegacyBukkitPlatformSchedulerTaskBuilder(this.plugin, this.handle);
    }

}
