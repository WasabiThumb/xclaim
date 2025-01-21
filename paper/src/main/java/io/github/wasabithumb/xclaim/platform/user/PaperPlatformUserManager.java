package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformPlayer;
import io.github.wasabithumb.xclaim.util.identity.Identities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PaperPlatformUserManager extends BukkitPlatformUserManager {

    public PaperPlatformUserManager(@NotNull BukkitPlatform platform) {
        super(platform);
    }

    @Override
    protected @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return Bukkit.getOfflinePlayerIfCached(name);
    }

    @Override
    protected boolean isDiscoverable(@NotNull Player player) {
        return Identities.get(player).isReal();
    }

    @Override
    protected @Nullable BukkitPlatformPlayer getPlayerByV3(@NotNull UUID uuid) {
        return this.adapter.player(Identities.reverse(uuid));
    }

}
