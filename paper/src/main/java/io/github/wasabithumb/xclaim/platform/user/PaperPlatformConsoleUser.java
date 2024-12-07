package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformConsoleUser extends PaperPlatformUser implements BukkitPlatformConsoleUser {

    public PaperPlatformConsoleUser(@NotNull PaperPlatform platform, @NotNull ConsoleCommandSender handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull ConsoleCommandSender handle() {
        return (ConsoleCommandSender) this.handle;
    }

}
