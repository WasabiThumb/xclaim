package io.github.wasabithumb.xclaim.platform.misc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformBossBar {

    float progress();

    @Contract("_ -> this")
    @NotNull PlatformBossBar progress(float value);

    void remove();

    //

    enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE
    }

    enum Overlay {
        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20
    }

}
