package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformPlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BukkitPlatformUser extends PlatformUser {

    @Override
    @NotNull CommandSender handle();

    @Override
    @NotNull BukkitPlatformPlayer asPlayer() throws UnsupportedOperationException;

    @Override
    default boolean isOp() {
        return this.handle().isOp();
    }

    @Override
    default boolean hasPermission(@NotNull String permission) {
        return this.handle().hasPermission(permission);
    }

}
