package codes.wasabi.xclaim.platform;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface PlatformScheduler {

    void synchronize(@NotNull Runnable task);

    @NotNull PlatformSchedulerTask runTaskTimer(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period);

    @NotNull PlatformSchedulerTask runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Runnable task, long delay, long period);

}
