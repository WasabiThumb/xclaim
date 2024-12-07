package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface PlatformUserManager {

    /**
     * Returns the console user.
     */
    @NotNull PlatformConsoleUser console();

    /**
     * Returns all online players.
     */
    @NotNull List<PlatformPlayer> players();

    /**
     * Returns a user by their UUID.
     * If the UUID is all 0, returns the {@link PlatformConsoleUser}.
     * If the UUID is that of an online player,
     * returns a {@link io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer PlatformPlayer}.
     * Otherwise, returns a {@link PlatformOfflineUser}. May block depending on implementation.
     */
    @NotNull PlatformUser getUser(@NotNull UUID uuid);

    /**
     * Attempts to match a user by their name.
     * Will never return the console user. May block depending on implementation.
     */
    @Nullable PlatformUser matchUser(@NotNull String name);

    /**
     * Returns an online player by their UUID.
     */
    @Nullable PlatformPlayer getPlayer(@NotNull UUID uuid);

}
