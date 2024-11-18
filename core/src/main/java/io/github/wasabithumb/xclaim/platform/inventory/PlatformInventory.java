package io.github.wasabithumb.xclaim.platform.inventory;

import org.jetbrains.annotations.Nullable;

public interface PlatformInventory<D> {

    int size();

    @Nullable PlatformItem getItem(int index);

    void setItem(int index, @Nullable PlatformItem item);

    default @Nullable D customData() {
        return null;
    }

}
