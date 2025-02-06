package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class SpongePlatformEntityDeathEvent
        extends SpongePlatformEvent<DestructEntityEvent.Death>
        implements PlatformEntityDeathEvent
{

    @Adapter
    public SpongePlatformEntityDeathEvent(
            @NotNull SpongePlatform platform,
            @NotNull DestructEntityEvent.Death handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull List<PlatformItem> drops() {
        // TODO: DropItemEvent.Destruct
        return new ArrayList<>();
    }

    @Override
    public @NotNull PlatformEntity entity() {
        return this.platform.adapter().entity(this.handle.entity());
    }

}
