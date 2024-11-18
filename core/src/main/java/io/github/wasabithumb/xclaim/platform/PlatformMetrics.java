package io.github.wasabithumb.xclaim.platform;

import org.bstats.charts.CustomChart;
import org.jetbrains.annotations.NotNull;

public interface PlatformMetrics extends PlatformObject {

    void addCustomChart(@NotNull CustomChart chart);

    void shutdown();

}
