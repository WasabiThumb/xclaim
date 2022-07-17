package codes.wasabi.xclaim.platform.paper;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class PaperAudiences implements BukkitAudiences {

    public PaperAudiences() {

    }

    @Override
    public @NotNull Audience sender(@NotNull CommandSender sender) {
        return sender;
    }

    @Override
    public @NotNull Audience player(@NotNull Player player) {
        return player;
    }

    @Override
    public @NotNull Audience filter(@NotNull Predicate<CommandSender> filter) {
        List<Audience> audienceList = new ArrayList<>();
        CommandSender cs = Bukkit.getConsoleSender();
        if (filter.test(cs)) audienceList.add(cs);
        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (filter.test(ply)) audienceList.add(ply);
        }
        return Audience.audience(audienceList);
    }

    @Override
    public @NotNull Audience all() {
        List<Audience> audienceList = new ArrayList<>();
        audienceList.add(Bukkit.getConsoleSender());
        audienceList.addAll(Bukkit.getOnlinePlayers());
        return Audience.audience(audienceList);
    }

    @Override
    public @NotNull Audience console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Audience players() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        Audience ret = Bukkit.getPlayer(playerId);
        if (ret == null) return Audience.empty();
        return ret;
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        List<Audience> audienceList = new ArrayList<>();
        CommandSender cs = Bukkit.getConsoleSender();
        if (cs.hasPermission(permission)) audienceList.add(cs);
        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (ply.hasPermission(permission)) audienceList.add(ply);
        }
        return Audience.audience(audienceList);
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        List<Audience> audienceList = new ArrayList<>();
        audienceList.add(Bukkit.getConsoleSender());
        World w = null;
        if (world instanceof NamespacedKey nk) {
            w = Bukkit.getWorld(nk);
        } else {
            for (World wld : Bukkit.getWorlds()) {
                if (wld.getKey().equals(world)) {
                    w = wld;
                    break;
                }
            }
        }
        if (w != null) {
            audienceList.addAll(w.getPlayers());
        }
        return Audience.audience(audienceList);
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return all();
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return ComponentFlattener.basic();
    }

    @Override
    public void close() { }

}
