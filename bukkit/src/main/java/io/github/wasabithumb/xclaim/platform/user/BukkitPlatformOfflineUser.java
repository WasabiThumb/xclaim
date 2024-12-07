package io.github.wasabithumb.xclaim.platform.user;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitPlatformOfflineUser extends PlatformOfflineUser {

    @ApiStatus.Internal
    public BukkitPlatformOfflineUser(@NotNull OfflinePlayer handle, @NotNull String name) {
        super(handle.getUniqueId(), name, handle);
    }

    @ApiStatus.Internal
    @ApiStatus.Obsolete
    public BukkitPlatformOfflineUser(@NotNull OfflinePlayer handle) {
        this(handle, Objects.requireNonNullElse(handle.getName(), "<i>???</i>"));
    }

    @Override
    public @NotNull OfflinePlayer handle() {
        return (OfflinePlayer) super.handle();
    }

}
