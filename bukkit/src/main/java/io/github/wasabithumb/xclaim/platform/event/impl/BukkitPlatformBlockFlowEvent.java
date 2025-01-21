package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.bukkit.event.block.BlockFromToEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformBlockFlowEvent extends BukkitPlatformEvent<BlockFromToEvent> implements PlatformBlockFlowEvent {

    public BukkitPlatformBlockFlowEvent(@NotNull BukkitPlatform platform, @NotNull BlockFromToEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformBlock block() {
        return this.platform.adapter().block(this.handle.getBlock());
    }

    @Override
    public @NotNull PlatformBlock targetBlock() {
        return this.platform.adapter().block(this.handle.getToBlock());
    }

}
