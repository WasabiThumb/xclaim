package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlatformOfflineUser implements PlatformUser {

    private final UUID uuid;
    private final String name;
    private final Object handle;

    @ApiStatus.Internal
    protected PlatformOfflineUser(@NotNull UUID uuid, @NotNull String name, @NotNull Object handle) {
        this.uuid = uuid;
        this.name = name;
        this.handle = handle;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
    }

    @Override
    public @NotNull String displayName() {
        return this.name;
    }

    @Override
    public boolean isOffline() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public @NotNull PlatformPlayer asPlayer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PlatformOfflineUser is not a player");
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    @Override
    public void sendMessage(@NotNull String message) { }

    @Override
    public @NotNull Object handle() {
        return this.handle;
    }

}
