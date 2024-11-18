package io.github.wasabithumb.xclaim.platform;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.NotNull;

public record BukkitPlatformMetrics(
        @NotNull Metrics handle
) implements PlatformMetrics {

    @Override
    public void addCustomChart(@NotNull CustomChart chart) {
        this.handle.addCustomChart(chart);
    }

    @Override
    public void shutdown() {
        this.handle.shutdown();
    }

}
