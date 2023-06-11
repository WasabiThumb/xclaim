package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import codes.wasabi.xclaim.util.ConfigUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class GraceRoutine implements Listener {

    private static GraceRoutine instance = null;
    private static boolean active = false;

    public static void start() {
        if (!active) {
            instance = new GraceRoutine();
            Bukkit.getPluginManager().registerEvents(instance, XClaim.instance);
            active = true;
        }
    }

    public static void stop() {
        if (active) {
            HandlerList.unregisterAll(instance);
            instance.timerTask.cancel();
            instance = null;
            active = false;
        }
    }

    public static void refresh() {
        Map<World, Boolean> allowedMap = new HashMap<>();
        for (Claim claim : Claim.getAll()) {
            World world = claim.getWorld();
            if (world == null) continue;
            Boolean allowed = allowedMap.get(world);
            if (allowed == null) {
                allowed = ConfigUtil.worldIsAllowed(XClaim.mainConfig, world);
                allowedMap.put(world, allowed);
            }
            if (!allowed) {
                start();
                return;
            }
        }
        stop();
    }

    private final PlatformSchedulerTask timerTask;
    private final long graceTimeMillis;
    private final BukkitAudiences adv = Platform.getAdventure();
    private GraceRoutine() {
        long graceTime = Math.max(XClaim.mainConfig.getLong("worlds.grace-time", 604800L), 0L);
        graceTimeMillis = graceTime * 1000L;
        // Approximate how often we need to run the routine in order to fall within the requested period
        // By default this will be 3024000 ticks or 1.75 days w/o lag (a quarter of a week) which is excessively long
        long ticks = Math.max((long) Math.floor((graceTime / 4d) * 20d), 1L);
        // So we provide a lower limit of 200 ticks (about 10 seconds without lag) which any server should be
        // able to handle
        ticks = Math.min(ticks, 200L);
        timerTask = Platform.get().getScheduler().runTaskTimerAsynchronously(XClaim.instance, this::evaluate, 0L, ticks);
    }

    private void evaluate() {
        long now = System.currentTimeMillis();
        int count = 0;
        Map<World, Boolean> allowedMap = new HashMap<>();
        for (Claim c : Claim.getAll()) {
            World world = c.getWorld();
            if (world == null) continue;
            Boolean allowed = allowedMap.get(world);
            if (allowed == null) {
                allowed = ConfigUtil.worldIsAllowed(XClaim.mainConfig, world);
                allowedMap.put(world, allowed);
            }
            if (!allowed) {
                count++;
                long start = c.getGraceStart();
                if (start <= 0) {
                    start = now;
                    c.setGraceStart(start);
                }
                long elapsed = now - start;
                if (elapsed >= graceTimeMillis) {
                    OfflinePlayer op = c.getOwner().getOfflinePlayer();
                    c.unclaim();
                    Player ply = op.getPlayer();
                    if (ply != null) {
                        adv.player(ply).sendMessage(XClaim.lang.getComponent("grace-remove", c.getName()));
                    }
                    count--;
                }
            }
        }
        if (count < 1) stop();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player ply = event.getPlayer();
        int count = 0;
        for (Claim c : Claim.getByOwner(ply)) {
            World world = c.getWorld();
            if (world == null) continue;
            if (!ConfigUtil.worldIsAllowed(XClaim.mainConfig, world)) {
                count++;
            }
        }
        if (count < 1) return;
        Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("grace-alert", count));
    }

}
