package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlatformConsoleUser extends PlatformUser {

    @Override
    default @NotNull UUID uuid() {
        return new UUID(0L, 0L);
    }

    @Override
    default @NotNull String displayName() {
        return "<gray><b>CONSOLE</b></gray>";
    }

    @Override
    default boolean isOffline() {
        return false;
    }

    @Override
    default boolean isPlayer() {
        return false;
    }

    @Override
    default @NotNull PlatformPlayer asPlayer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("CONSOLE is not a player!");
    }

    @Override
    default boolean isOp() {
        return true;
    }

    @Override
    default boolean hasPermission(@NotNull String permission) {
        return true;
    }

}
