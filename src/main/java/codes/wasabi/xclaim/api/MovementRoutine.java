package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ChunkReference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MovementRoutine implements Listener {

    private static MovementRoutine instance;
    private static boolean initialized = false;
    public static MovementRoutine getInstance() {
        return instance;
    }

    public static void initialize() {
        if (initialized) return;
        instance = new MovementRoutine();
        Bukkit.getPluginManager().registerEvents(instance, XClaim.instance);
        initialized = true;
    }

    public static void cleanup() {
        if (!initialized) return;
        HandlerList.unregisterAll(instance);
        instance = null;
        initialized = false;
    }

    private MovementRoutine() { }

    private boolean chunkSemanticEquals(Object a, Object b) {
        int aType = (a instanceof Chunk ? 0 : (a instanceof ChunkReference ? 1 : 2));
        int bType = (b instanceof Chunk ? 0 : (b instanceof ChunkReference ? 1 : 2));

        if (aType > 1 || bType > 1) {
            return aType == bType;
        }

        ChunkReference ar = (aType == 1) ? (ChunkReference) a : ChunkReference.ofChunk((Chunk) a);
        ChunkReference br = (bType == 1) ? (ChunkReference) b : ChunkReference.ofChunk((Chunk) b);

        return Objects.equals(ar, br);
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        Chunk fromChunk = from.getChunk();
        Chunk toChunk = to.getChunk();
        if (chunkSemanticEquals(fromChunk, toChunk)) return;
        Claim fromClaim = null;
        Claim toClaim = null;
        boolean fromSet = false;
        boolean toSet = false;
        for (Claim candidate : Claim.getAll()) {
            for (ChunkReference c : candidate.getChunks()) {
                if (chunkSemanticEquals(c, fromChunk)) {
                    fromClaim = candidate;
                    fromSet = true;
                    continue;
                }
                if (chunkSemanticEquals(c, toChunk)) {
                    toClaim = candidate;
                    toSet = true;
                }
            }
            if (fromSet && toSet) break;
        }
        Player ply = event.getPlayer();
        if (toSet) {
            if (!toClaim.equals(fromClaim)) {
                XCPlayer claimOwner = toClaim.getOwner();
                Player online = claimOwner.getPlayer();
                Component name;
                if (online != null) {
                    name = Platform.get().playerDisplayName(online);
                } else {
                    String n = claimOwner.getName();
                    if (n == null) n = XClaim.lang.get("unknown");
                    name = Component.text(n);
                }
                Platform.get().sendActionBar(ply, XClaim.lang.getComponent(
                        "move-enter",
                        name, Component.text(toClaim.getName())
                ));
            }
        } else if (fromSet) {
            Platform.get().sendActionBar(ply, XClaim.lang.getComponent(
                    "move-exit",
                    fromClaim.getName()
            ));
        }
    }

}
