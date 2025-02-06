package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SpongePlatformPlayerQuitEvent
        extends SpongePlatformEvent<ServerSideConnectionEvent.Disconnect>
        implements PlatformPlayerJoinEvent
{

    @Adapter
    public SpongePlatformPlayerQuitEvent(
            @NotNull SpongePlatform platform,
            @NotNull ServerSideConnectionEvent.Disconnect handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.player());
    }

}
