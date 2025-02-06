package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SpongePlatformPlayerJoinEvent
        extends SpongePlatformEvent<ServerSideConnectionEvent.Join>
        implements PlatformPlayerJoinEvent
{

    @Adapter
    public SpongePlatformPlayerJoinEvent(
            @NotNull SpongePlatform platform,
            @NotNull ServerSideConnectionEvent.Join handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.player());
    }

}
