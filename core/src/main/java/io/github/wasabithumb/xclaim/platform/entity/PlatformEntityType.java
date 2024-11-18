package io.github.wasabithumb.xclaim.platform.entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformEntityType {

    @Contract(pure = true)
    @NotNull String name();

}
