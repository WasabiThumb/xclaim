package io.github.wasabithumb.xclaim.platform;

import org.bstats.charts.CustomChart;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;

public record SpongePlatformMetrics(
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
