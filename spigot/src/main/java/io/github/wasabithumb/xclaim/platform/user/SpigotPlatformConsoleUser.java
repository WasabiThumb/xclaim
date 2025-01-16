package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformConsoleUser extends SpigotPlatformUser implements BukkitPlatformConsoleUser {

    public SpigotPlatformConsoleUser(@NotNull SpigotPlatform platform, @NotNull ConsoleCommandSender handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull ConsoleCommandSender handle() {
        return (ConsoleCommandSender) this.handle;
    }

}
