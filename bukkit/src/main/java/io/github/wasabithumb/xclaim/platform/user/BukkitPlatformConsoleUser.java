package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformPlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlatformConsoleUser extends BukkitPlatformUser, PlatformConsoleUser {

    @Override
    @NotNull ConsoleCommandSender handle();

    @Override
    default @NotNull BukkitPlatformPlayer asPlayer() throws UnsupportedOperationException {
        PlatformConsoleUser.super.asPlayer();
        return null;
    }

    @Override
    default boolean isOp() {
        return BukkitPlatformUser.super.isOp();
    }

    @Override
    default boolean hasPermission(@NotNull String permission) {
        return BukkitPlatformUser.super.hasPermission(permission);
    }

}