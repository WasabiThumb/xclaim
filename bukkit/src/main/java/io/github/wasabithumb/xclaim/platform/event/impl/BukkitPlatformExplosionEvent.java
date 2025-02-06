package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformEntityEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitPlatformExplosionEvent extends BukkitPlatformEntityEvent<EntityExplodeEvent> implements PlatformExplosionEvent {

    public BukkitPlatformExplosionEvent(@NotNull BukkitPlatform platform, @NotNull EntityExplodeEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull List<PlatformBlock> blocks() {
        List<Block> backing = this.handle.blockList();
        return new ProxyList<>(
                backing,
                (Block b) -> this.platform.adapter().block(b),
                (PlatformBlock pb) -> this.platform.adapter().block(pb)
        );
    }

}
