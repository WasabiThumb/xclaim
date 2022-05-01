package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

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

    private MovementRoutine() { }

    private boolean chunkSemanticEquals(Chunk a, Chunk b) {
        if (a.getX() != b.getX()) return false;
        if (a.getZ() != b.getZ()) return false;
        return a.getWorld().getName().equals(b.getWorld().getName());
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Chunk fromChunk = from.getChunk();
        Chunk toChunk = to.getChunk();
        if (chunkSemanticEquals(fromChunk, toChunk)) return;
        Claim fromClaim = null;
        Claim toClaim = null;
        boolean fromSet = false;
        boolean toSet = false;
        for (Claim candidate : Claim.getAll()) {
            for (Chunk c : candidate.getChunks()) {
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
                OfflinePlayer claimOwner = toClaim.getOwner();
                Player online = claimOwner.getPlayer();
                Component name;
                if (online != null) {
                    name = online.displayName();
                } else {
                    String n = claimOwner.getName();
                    if (n == null) n = "Unknown";
                    name = Component.text(n);
                }
                ply.sendActionBar(Component.empty()
                        .append(Component.text("Entering ").color(NamedTextColor.WHITE))
                        .append(name.color(NamedTextColor.GOLD))
                        .append(Component.text("'s ").color(NamedTextColor.WHITE))
                        .append(Component.text(toClaim.getName()).color(NamedTextColor.GREEN))
                );
            }
        } else if (fromSet) {
            ply.sendActionBar(Component.empty()
                    .append(Component.text("Leaving ").color(NamedTextColor.WHITE))
                    .append(Component.text(fromClaim.getName()).color(NamedTextColor.GREEN))
            );
        }
    }

}
