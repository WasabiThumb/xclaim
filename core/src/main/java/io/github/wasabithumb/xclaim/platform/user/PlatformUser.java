package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlatformUser extends PlatformObject {

    @NotNull UUID uuid();

    @NotNull String displayName();

    boolean isOffline();

    boolean isPlayer();

    @NotNull PlatformPlayer asPlayer() throws UnsupportedOperationException;

    boolean isOp();

    /** Should take into account operator status (if applicable)! */
    boolean hasPermission(@NotNull String permission);

    void sendMessage(@NotNull String message);

}
