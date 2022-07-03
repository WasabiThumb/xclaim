package codes.wasabi.xclaim.command.argument.type;

import codes.wasabi.xclaim.XClaim;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OfflinePlayerType extends Type<OfflinePlayer> {

    public static class PlayerUpdateListener implements Listener {

        @EventHandler
        public void onJoin(@NotNull PlayerLoginEvent event) {
            Player ply = event.getPlayer();
            if (!ply.hasPlayedBefore()) {
                OfflinePlayer[] newOfflinePlayers = new OfflinePlayer[offlinePlayers.length + 1];
                System.arraycopy(offlinePlayers, 0, newOfflinePlayers, 1, offlinePlayers.length);
                newOfflinePlayers[0] = ply;
                offlinePlayers = newOfflinePlayers;
            }
        }

    }

    private static OfflinePlayer[] offlinePlayers = new OfflinePlayer[0];
    private static PlayerUpdateListener listener = null;

    public static boolean initializeListener() {
        if (listener != null) return false;
        offlinePlayers = Bukkit.getOfflinePlayers();
        listener = new PlayerUpdateListener();
        Bukkit.getPluginManager().registerEvents(listener, XClaim.instance);
        return true;
    }

    public static boolean clearListener() {
        if (listener == null) return false;
        HandlerList.unregisterAll(listener);
        listener = null;
        return true;
    }

    @Override
    public @NotNull Class<OfflinePlayer> getTypeClass() {
        return OfflinePlayer.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return XClaim.lang.get("arg-offlinePlayer-name");
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        Set<String> ret = new HashSet<>();
        for (OfflinePlayer op : offlinePlayers) {
            String name = op.getName();
            if (name != null) ret.add(name);
        }
        return ret;
    }

    @Override
    protected @NotNull OfflinePlayer convert(@NotNull String string) throws NullPointerException {
        OfflinePlayer uncased = null;
        for (OfflinePlayer ply : offlinePlayers) {
            String name = ply.getName();
            if (name == null) continue;
            if (name.equalsIgnoreCase(string)) {
                uncased = ply;
                if (name.equals(string)) return ply;
            }
        }
        return Objects.requireNonNull(uncased);
    }

}