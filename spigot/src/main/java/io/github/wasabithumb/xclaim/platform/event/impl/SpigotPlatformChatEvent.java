package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import io.github.wasabithumb.xclaim.platform.entity.SpigotPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.SpigotPlatformEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformChatEvent extends SpigotPlatformEvent<AsyncPlayerChatEvent> implements PlatformChatEvent {

    public SpigotPlatformChatEvent(@NotNull SpigotPlatform platform, @NotNull AsyncPlayerChatEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull SpigotPlatformPlayer player() {
        return this.platform().adapter().player(this.handle.getPlayer());
    }

    @Override
    public @NotNull String message() {
        return this.platform().mm().serialize(
                LegacyComponentSerializer.legacySection().deserialize(this.handle.getMessage())
        );
    }

    @Override
    public @NotNull String plainMessage() {
        return PlainTextComponentSerializer.plainText().serialize(
                LegacyComponentSerializer.legacySection().deserialize(this.handle.getMessage())
        );
    }

}
