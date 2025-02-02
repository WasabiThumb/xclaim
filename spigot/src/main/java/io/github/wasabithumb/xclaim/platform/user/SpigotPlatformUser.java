package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import io.github.wasabithumb.xclaim.platform.entity.SpigotPlatformPlayer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpigotPlatformUser implements BukkitPlatformUser {

    public static @NotNull BukkitPlatformUser of(@NotNull SpigotPlatform platform, @NotNull CommandSender sender) {
        if (sender instanceof Player ply) {
            return new SpigotPlatformPlayer(platform, ply);
        } else if (sender instanceof ConsoleCommandSender console) {
            return new SpigotPlatformConsoleUser(platform, console);
        } else {
            return new SpigotPlatformUser(platform, sender);
        }
    }

    //

    protected final SpigotPlatform platform;
    protected final CommandSender handle;
    SpigotPlatformUser(@NotNull SpigotPlatform platform, @NotNull CommandSender handle) {
        this.platform = platform;
        this.handle = handle;
    }

    @Override
    public @NotNull CommandSender handle() {
        return this.handle;
    }

    @Override
    public @NotNull UUID uuid() {
        return PlatformConsoleUser.ID;
    }

    @Override
    public @NotNull String displayName() {
        return this.platform.mm().serialize(LegacyComponentSerializer.legacySection().deserialize(this.handle.getName()));
    }

    @Override
    public boolean isOffline() {
        return false;
    }

    @Override
    public boolean isPlayer() {
        return this.handle instanceof Player;
    }

    @Override
    public @NotNull SpigotPlatformPlayer asPlayer() throws UnsupportedOperationException {
        return this.platform.adapter().player(this.handle);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.platform.audiences().sender(this.handle).sendMessage(this.platform.mm().deserialize(message));
    }

}
