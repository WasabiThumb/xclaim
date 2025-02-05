package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.BukkitPlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.util.MojAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class BukkitPlatformUserManager implements PlatformUserManager {

    protected final BukkitPlatformTypeAdapter adapter;
    protected final Logger logger;
    public BukkitPlatformUserManager(@NotNull BukkitPlatform platform) {
        this.adapter = platform.adapter();
        this.logger = platform.plugin().logger();
    }

    @Override
    public @NotNull BukkitPlatformConsoleUser console() {
        return this.adapter.consoleUser(Bukkit.getConsoleSender());
    }

    @Override
    public @NotNull List<PlatformPlayer> players() {
        Collection<? extends Player> base = Bukkit.getOnlinePlayers();
        List<PlatformPlayer> ret = new ArrayList<>(base.size());
        for (Player bp : base) ret.add(this.adapter.player(bp));
        return Collections.unmodifiableList(ret);
    }

    @Override
    public int playerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Contract("null -> null; !null -> !null")
    public PlatformUser fromOfflinePlayer(OfflinePlayer op) {
        if (op == null) return null;
        if (op instanceof Player ply && this.isDiscoverable(ply)) {
            return this.adapter.player(ply);
        } else {
            return this.adapter.offlineUser(op);
        }
    }

    @Override
    public @NotNull PlatformUser getUser(@NotNull UUID uuid) {
        if (uuid.version() == 3) {
            PlatformUser v3 = this.getPlayerByV3(uuid);
            if (v3 != null) return v3;
        }
        return this.fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public @Nullable PlatformUser matchUser(@NotNull String name) {
        OfflinePlayer ply = Bukkit.getPlayer(name);
        if (ply != null && this.isDiscoverable((Player) ply)) return this.adapter.player(ply);

        ply = this.getOfflinePlayerIfCached(name);
        if (ply != null) return this.adapter.offlineUser(ply);

        UUID uuid = MojAPI.api(this.logger).getProfile(name);
        if (uuid == null) return null;
        return new BukkitPlatformOfflineUser(Bukkit.getOfflinePlayer(uuid), name);
    }

    @Override
    public @Nullable BukkitPlatformPlayer getPlayer(@NotNull UUID uuid) {
        if (uuid.version() == 3) return this.getPlayerByV3(uuid);
        Player ply = Bukkit.getPlayer(uuid);
        if (ply == null || !this.isDiscoverable(ply)) return null;
        return this.adapter.player(ply);
    }

    protected @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return null;
    }

    protected boolean isDiscoverable(@NotNull Player player) {
        return true;
    }

    protected @Nullable BukkitPlatformPlayer getPlayerByV3(@NotNull UUID uuid) {
        return null;
    }

}
