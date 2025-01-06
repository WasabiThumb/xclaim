package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.PlatformObject;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformInventory extends PlatformObject {

    @NotNull Platform platform();

    int size();

    @Nullable PlatformItem @NotNull [] getContents();

    void setContents(@Nullable PlatformItem @NotNull [] contents);

    @Nullable PlatformItem getItem(int index);

    @Nullable PlatformItem getItem(@NotNull PlatformEquipmentSlot slot);

    void setItem(int index, @Nullable PlatformItem item);

    void setItem(@NotNull PlatformEquipmentSlot slot, @Nullable PlatformItem item);

    @Nullable PlatformBlock getContainerBlock();

    void clear();

}
