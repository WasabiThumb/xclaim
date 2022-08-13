package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

public class ExplosionHandler extends PermissionHandler {

    public ExplosionHandler(@NotNull Claim claim) {
        super(claim);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Claim claim = getClaim();
        TrustLevel tl = claim.getPermission(Permission.EXPLODE);
        if (tl.equals(TrustLevel.ALL)) return;
        Entity e = event.getEntity();
        if (!claim.contains(e.getLocation())) return;
        if (tl.equals(TrustLevel.NONE)) {
            event.setCancelled(true);
            return;
        }
        if (e instanceof Creeper) {
            Creeper creeper = (Creeper) e;
            LivingEntity target = creeper.getTarget();
            boolean targetPrivileged = false;
            if (target != null) {
                if (target instanceof Player) {
                    Player pl = (Player) target;
                    targetPrivileged = claim.hasPermission(pl, Permission.EXPLODE);
                }
            }
            if (!targetPrivileged) {
                event.setCancelled(true);
                World w = e.getWorld();
                Location loc = e.getLocation();
                Platform.get().createExplosion(w, loc, creeper.isPowered() ? 6 : 3, false, false, creeper);
                e.remove();
            }
        } else {
            if (e instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) e;
                Entity source = tnt.getSource();
                if (source != null) {
                    if (source.isValid()) {
                        if (source instanceof Player) {
                            Player pl = (Player) source;
                            if (claim.hasPermission(pl, Permission.EXPLODE)) {
                                return;
                            }
                            stdError(pl);
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

}
