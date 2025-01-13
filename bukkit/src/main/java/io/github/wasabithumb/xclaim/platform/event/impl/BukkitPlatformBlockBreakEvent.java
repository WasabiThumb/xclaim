package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformBlockBreakEvent extends BukkitPlatformEvent<BlockBreakEvent> implements PlatformBlockBreakEvent {

    public BukkitPlatformBlockBreakEvent(@NotNull BukkitPlatform platform, @NotNull BlockBreakEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.getPlayer());
    }

    @Override
    public @NotNull PlatformBlock block() {
        return this.platform.adapter().block(this.handle.getBlock());
    }

}
