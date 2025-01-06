package io.github.wasabithumb.xclaim.platform.user;

import com.google.common.collect.ImmutableList;
import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.BukkitPlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.BukkitPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.util.ProxyList;
import io.github.wasabithumb.xclaim.util.RemotePlayers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BukkitPlatformUserManager implements PlatformUserManager {

    private final BukkitPlatformTypeAdapter adapter;
    public BukkitPlatformUserManager(@NotNull BukkitPlatform platform) {
        this.adapter = platform.adapter();
    }

    @Override
    public @NotNull BukkitPlatformConsoleUser console() {
        return this.adapter.consoleUser(Bukkit.getConsoleSender());
    }

    @Override
    public @NotNull List<PlatformPlayer> players() {
        List<? extends Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        return new ProxyList<>(players, this.adapter::player);
    }

    @Override
    public int playerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Contract("null -> null; !null -> !null")
    public PlatformUser fromOfflinePlayer(OfflinePlayer op) {
        if (op == null) return null;
        if (op instanceof Player ply) {
            return this.adapter.player(ply);
        } else {
            return this.adapter.offlineUser(op);
        }
    }

    @Override
    public @NotNull PlatformUser getUser(@NotNull UUID uuid) {
        return this.fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public @Nullable PlatformUser matchUser(@NotNull String name) {
        OfflinePlayer ply = Bukkit.getPlayer(name);
        if (ply != null) return this.adapter.player(ply);
        ply = this.getOfflinePlayerIfCached(name);
        if (ply != null) return this.adapter.offlineUser(ply);
        ply = RemotePlayers.fetch(name);
        if (ply == null) return null;
        return new BukkitPlatformOfflineUser(ply, name);
    }

    @Override
    public @Nullable BukkitPlatformPlayer getPlayer(@NotNull UUID uuid) {
        return this.adapter.player(Bukkit.getPlayer(uuid));
    }

    protected @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return null;
    }

}
