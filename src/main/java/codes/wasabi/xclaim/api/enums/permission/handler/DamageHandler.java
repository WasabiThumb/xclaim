package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DamageHandler extends PermissionHandler {

    public static final class All extends DamageHandler {
        public All(@NotNull Claim claim) {
            super(claim, (Entity e) -> true, Permission.ENTITY_DAMAGE);
        }
    }

    public static final class Living extends DamageHandler {
        public Living(@NotNull Claim claim) {
            super(claim, (Entity e) -> e instanceof LivingEntity, Permission.LIVING_ENTITY_DAMAGE);
        }
    }

    public static final class Mob extends DamageHandler {
        public Mob(@NotNull Claim claim) {
            super(claim, (Entity e) -> e instanceof org.bukkit.entity.Mob, Permission.MOB_DAMAGE);
        }
    }

    public static final class Friendly extends DamageHandler {
        public Friendly(@NotNull Claim claim) {
            super(claim, (Entity e) -> e instanceof Animals, Permission.FRIENDLY_MOB_DAMAGE);
        }
    }

    private final Predicate<Entity> filter;
    private final Permission permission;
    private final boolean isDefault;
    public DamageHandler(@NotNull Claim claim, @NotNull Predicate<Entity> filter, @NotNull Permission permission) {
        super(claim);
        this.filter = filter;
        this.permission = permission;
        this.isDefault = permission == Permission.ENTITY_DAMAGE;
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player ply) {
            if (getClaim().hasPermission(ply, permission)) return;
            if (!isDefault) {
                if (getClaim().hasPermission(ply, Permission.ENTITY_DAMAGE)) return;
            }
            Entity victim = event.getEntity();
            if (!filter.test(victim)) return;
            if (!getClaim().contains(victim.getLocation())) return;
            event.setCancelled(true);
            stdError(ply);
        }
    }

}
