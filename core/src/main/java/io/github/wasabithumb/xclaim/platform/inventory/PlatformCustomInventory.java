package io.github.wasabithumb.xclaim.platform.inventory;

import org.jetbrains.annotations.NotNull;

public interface PlatformCustomInventory<T> extends PlatformInventory {

    @NotNull T data();

}
