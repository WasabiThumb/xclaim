package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.message.PlayerChatEvent;

public class SpongePlatformChatEvent
        extends SpongePlatformEvent<PlayerChatEvent>
        implements PlatformChatEvent
{

    @Adapter
    public SpongePlatformChatEvent(
            @NotNull SpongePlatform platform,
            @NotNull PlayerChatEvent handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull String message() {
        return this.platform.mm().serialize(this.handle.message());
    }

    @Override
    public @NotNull String plainMessage() {
        return PlainTextComponentSerializer.plainText().serialize(this.handle.message());
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
