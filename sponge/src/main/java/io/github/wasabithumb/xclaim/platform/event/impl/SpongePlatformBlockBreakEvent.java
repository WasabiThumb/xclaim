package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class SpongePlatformBlockBreakEvent
        extends SpongePlatformEvent<InteractBlockEvent.Primary.Finish>
        implements PlatformBlockBreakEvent
{

    @Adapter
    public SpongePlatformBlockBreakEvent(
            @NotNull SpongePlatform platform,
            @NotNull InteractBlockEvent.Primary.Finish handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

    @Override
    public @NotNull SpongePlatformBlock block() {
        return SpongePlatformBlock.of(this.platform, this.handle.block());
    }

}
