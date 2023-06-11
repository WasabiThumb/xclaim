package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EnterHandler extends PermissionHandler {

    public EnterHandler(@NotNull Claim claim) {
        super(claim);
    }

    private final Set<Player> occluding = new HashSet<>();

    private PlatformSchedulerTask task = null;
    @Override
    protected void onRegister() {
        occluding.clear();
        task = Platform.get().getScheduler().runTaskTimer(XClaim.instance, () -> {
            for (Player ply : occluding) {
                ply.damage(2);
            }
        }, 0L, 5L);
    }

    @Override
    protected void onUnregister() {
        if (task == null) return;
        if (!task.isCancelled()) task.cancel();
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player ply = event.getPlayer();
        if (getClaim().hasPermission(ply, Permission.ENTER)) return;
        //
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        boolean fromWithin = getClaim().contains(from);
        boolean toWithin = getClaim().contains(to);
        //
        boolean occ = toWithin && fromWithin;
        if (!occ) {
            occluding.remove(ply);
            if (toWithin) {
                event.setTo(from);
                event.setCancelled(true);
                stdError(ply);
            }
        } else {
            occluding.add(ply);
        }
    }

}
