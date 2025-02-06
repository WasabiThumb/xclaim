package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformBlock;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Queue;

public class SpongePlatformBlockFlowEvent
        extends SpongePlatformEvent<ChangeBlockEvent.All>
        implements PlatformBlockFlowEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull ChangeBlockEvent.All handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        for (BlockTransaction bt : handle.transactions()) {
            if (!RegistryUtil.referenceEquals(RegistryTypes.OPERATION, bt.operation(), Operations.LIQUID_SPREAD))
                continue;
            queue.add(new SpongePlatformBlockFlowEvent(platform, handle, bt));
        }
    }

    //

    private final BlockTransaction transaction;
    private SpongePlatformBlockFlowEvent(
            @NotNull SpongePlatform platform,
            @NotNull ChangeBlockEvent.All handle,
            @NotNull BlockTransaction transaction
    ) {
        super(platform, handle);
        this.transaction = transaction;
    }

    //

    @Override
    public @NotNull PlatformBlock block() {
        return SpongePlatformBlock.of(this.platform, this.transaction.original());
    }

    @Override
    public @NotNull PlatformBlock targetBlock() {
        return SpongePlatformBlock.of(this.platform, this.transaction.finalReplacement());
    }

}
