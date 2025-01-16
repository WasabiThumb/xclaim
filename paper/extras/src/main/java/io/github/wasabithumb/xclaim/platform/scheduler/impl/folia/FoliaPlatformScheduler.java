package io.github.wasabithumb.xclaim.platform.scheduler.impl.folia;

import io.github.wasabithumb.xclaim.platform.BukkitPlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.impl.folia.task.FoliaPlatformSchedulerTaskBuilder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class FoliaPlatformScheduler implements BukkitPlatformScheduler {

    private final Plugin plugin;
    private final BukkitPlatformTypeAdapter adapter;
    private final FoliaPlatformSchedulerFacets facets;

    @ApiStatus.Internal
    public FoliaPlatformScheduler(@NotNull Plugin plugin, @NotNull BukkitPlatformTypeAdapter adapter) {
        this.plugin = plugin;
        this.adapter = adapter;
        this.facets = new FoliaPlatformSchedulerFacets();
    }

    //

    @Override
    public @NotNull FoliaPlatformSchedulerTaskBuilder newTask() {
        return new FoliaPlatformSchedulerTaskBuilder(this.plugin, this.adapter, this.facets);
    }

}
