package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.util.ProxyList;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class BukkitPlatformBlockMultiPlaceEvent extends BukkitPlatformEvent<BlockMultiPlaceEvent> implements PlatformBlockMultiPlaceEvent {

    public BukkitPlatformBlockMultiPlaceEvent(@NotNull BukkitPlatform platform, @NotNull BlockMultiPlaceEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.getPlayer());
    }

    @Override
    public @NotNull Collection<PlatformBlock> blocks() {
        List<BlockState> backing = this.handle.getReplacedBlockStates();
        return new ProxyList<>(
                backing,
                (BlockState bs) -> this.platform.adapter().block(bs.getBlock())
        );
    }

}
