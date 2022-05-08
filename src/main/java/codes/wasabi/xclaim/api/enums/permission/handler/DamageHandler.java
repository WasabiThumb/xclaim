package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.EntityGroup;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class DamageHandler extends PermissionHandler {

    public static final class Friendly extends DamageHandler {
        public Friendly(@NotNull Claim claim) {
            super(claim, EntityGroup.FRIENDLY, Permission.ENTITY_DAMAGE_FRIENDLY);
        }
    }

    public static final class Hostile extends DamageHandler {
        public Hostile(@NotNull Claim claim) {
            super(claim, EntityGroup.HOSTILE, Permission.ENTITY_DAMAGE_FRIENDLY);
        }
    }

    public static final class Vehicle extends DamageHandler {
        public Vehicle(@NotNull Claim claim) {
            super(claim, EntityGroup.VEHICLE, Permission.ENTITY_DAMAGE_VEHICLE);
        }
    }

    public static final class NonLiving extends DamageHandler {
        public NonLiving(@NotNull Claim claim) {
            super(claim, EntityGroup.NOT_ALIVE, Permission.ENTITY_DAMAGE_NL);
        }
    }

    public static final class Misc extends DamageHandler {
        public Misc(@NotNull Claim claim) {
            super(claim, EntityGroup.MISC, Permission.ENTITY_DAMAGE_MISC);
        }
    }

    private final EntityGroup eg;
    private final Permission permission;
    public DamageHandler(@NotNull Claim claim, @NotNull EntityGroup eg, @NotNull Permission permission) {
        super(claim);
        this.eg = eg;
        this.permission = permission;
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        Player ply = null;
        boolean isPlayer = false;
        Entity damager = event.getDamager();
        if (damager instanceof Player p) {
            ply = p;
            isPlayer = true;
        } else if (damager instanceof Projectile projectile) {
            ProjectileSource ps = projectile.getShooter();
            if (ps instanceof Player p) {
                ply = p;
                isPlayer = true;
            }
        }
        if (isPlayer) {
            Claim c = getClaim();
            if (c.hasPermission(ply, permission)) return;
            Entity victim = event.getEntity();
            if (!eg.contains(victim)) return;
            if (!c.contains(victim.getLocation())) return;
            event.setCancelled(true);
            stdError(ply);
        }
    }

}
