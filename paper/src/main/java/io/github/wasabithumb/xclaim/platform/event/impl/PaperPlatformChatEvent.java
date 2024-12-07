package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PaperPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PaperPlatformEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformChatEvent extends PaperPlatformEvent<AsyncChatEvent> implements PlatformChatEvent {

    public PaperPlatformChatEvent(@NotNull PaperPlatform platform, @NotNull AsyncChatEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PaperPlatformPlayer player() {
        return this.platform().adapter().player(this.handle.getPlayer());
    }

    @Override
    public @NotNull String message() {
        return this.platform().mm().serialize(this.handle.message());
    }

    @Override
    public @NotNull String plainMessage() {
        return PlainTextComponentSerializer.plainText().serialize(this.handle.originalMessage());
    }

}
