package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPlatformUserManager extends BukkitPlatformUserManager {

    public PaperPlatformUserManager(@NotNull BukkitPlatform platform) {
        super(platform);
    }

    @Override
    protected @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return Bukkit.getOfflinePlayerIfCached(name);
    }

}
