package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.SpongePlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Queue;

public class SpongePlatformHangingBreakEvent
        extends SpongePlatformEvent<DestructEntityEvent>
        implements PlatformHangingBreakEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull DestructEntityEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!(handle.entity() instanceof Hanging)) return;
        queue.add(new SpongePlatformHangingBreakEvent(platform, handle));
    }

    //

    private SpongePlatformHangingBreakEvent(
            @NotNull SpongePlatform platform,
            @NotNull DestructEntityEvent handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformEntity entity() {
        return this.platform.adapter().entity(this.handle.entity());
    }

    @Override
    public @Nullable PlatformEntity remover() {
        SpongePlatformTypeAdapter adapter = this.platform.adapter();
        return this.handle.cause()
                .first(Entity.class)
                .map(adapter::entity)
                .orElse(null);
    }

}
