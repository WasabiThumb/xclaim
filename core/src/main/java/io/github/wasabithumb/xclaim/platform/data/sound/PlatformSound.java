package io.github.wasabithumb.xclaim.platform.data.sound;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformSound {

    @Contract(pure = true)
    @NotNull String name();

}
