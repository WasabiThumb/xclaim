package io.github.wasabithumb.xclaim.platform.scheduler.impl.folia;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record FoliaPlatformSchedulerFacets(
        @NotNull GlobalRegionScheduler global,
        @NotNull RegionScheduler region,
        @NotNull AsyncScheduler async
) {

    public FoliaPlatformSchedulerFacets() {
        this(
                Bukkit.getGlobalRegionScheduler(),
                Bukkit.getRegionScheduler(),
                Bukkit.getAsyncScheduler()
        );
    }

    public @NotNull EntityScheduler entity(@NotNull Entity entity) {
        return entity.getScheduler();
    }

}
