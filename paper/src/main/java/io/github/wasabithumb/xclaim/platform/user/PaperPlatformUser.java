package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PaperPlatformPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PaperPlatformUser implements BukkitPlatformUser {

    public static @NotNull BukkitPlatformUser of(@NotNull PaperPlatform platform, @NotNull CommandSender sender) {
        if (sender instanceof Player ply) {
            return new PaperPlatformPlayer(platform, ply);
        } else if (sender instanceof ConsoleCommandSender console) {
            return new PaperPlatformConsoleUser(platform, console);
        } else {
            return new PaperPlatformUser(platform, sender);
        }
    }

    //

    protected final PaperPlatform platform;
    protected final CommandSender handle;
    PaperPlatformUser(@NotNull PaperPlatform platform, @NotNull CommandSender handle) {
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
        return this.platform.mm().serialize(this.handle.name());
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
    public @NotNull PaperPlatformPlayer asPlayer() throws UnsupportedOperationException {
        return this.platform.adapter().player(this.handle);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.handle.sendMessage(this.platform.mm().deserialize(message));
    }

}
