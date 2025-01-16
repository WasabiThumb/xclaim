package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.BukkitPlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitPlatformEntity implements PlatformEntity {

    protected final BukkitPlatform platform;
    protected final Entity handle;
    public BukkitPlatformEntity(@NotNull BukkitPlatform platform, @NotNull Entity handle) {
        this.platform = platform;
        this.handle = handle;
    }

    @Override
    public @NotNull Entity handle() {
        return this.handle;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.handle.getUniqueId();
    }

    @Override
    public @NotNull BukkitPlatformEntityType type() {
        return new BukkitPlatformEntityType(this.handle.getType());
    }

    @Override
    public @NotNull BukkitPlatformPersistentDataContainer pdc() {
        return new BukkitPlatformPersistentDataContainer(this.platform, this.handle.getPersistentDataContainer());
    }

    @Override
    public @NotNull PlatformLocation location() {
        return this.platform.adapter().location(this.handle.getLocation());
    }

    @Override
    public void teleport(@NotNull PlatformLocation location) {
        this.handle.teleport(this.platform.adapter().location(location), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isProjectile() {
        return this.handle instanceof Projectile;
    }

    @Override
    public boolean isExplosive() {
        return this.handle instanceof Explosive;
    }

    public @Nullable BukkitPlatformPlayer sourcePlayer() {
        if (this.handle instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) return this.platform.adapter().player(shooter);
        } else if (this.handle instanceof TNTPrimed tnt) {
            Entity source = tnt.getSource();
            if (source != null && source.isValid() && source instanceof Player)
                return this.platform.adapter().player(source);
        } else if (this.handle instanceof EnderCrystal crystal) {
            EntityDamageEvent dmg = crystal.getLastDamageCause();
            if (dmg == null) return null;
            if (!(dmg instanceof EntityDamageByEntityEvent byEntity)) return null;
            Entity damager = byEntity.getDamager();
            if (damager instanceof Player)
                return this.platform.adapter().player(damager);
        }
        return null;
    }

    @Override
    public boolean isInGroup(@NotNull PlatformEntityGroup group) {
        return BukkitPlatformEntityGroupCheck.test(group, this.handle);
    }

    @Override
    public void damage(double damage) {
        if (this.handle instanceof Damageable dmg) {
            dmg.damage(damage);
        }
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        Vector cur = this.handle.getVelocity();
        this.handle.setVelocity(new Vector(
                cur.getX() + x,
                cur.getY() + y,
                cur.getZ() + z
        ));
    }

    @Override
    public @Nullable BukkitPlatformEntity target() {
        if (this.handle instanceof Mob mob) {
            return this.platform.adapter().entity(mob.getTarget());
        }
        return null;
    }

    @Override
    public boolean isPoweredCreeper() {
        if (this.handle instanceof Creeper c) {
            return c.isPowered();
        }
        return false;
    }

    @Override
    public void remove() {
        this.handle.remove();
    }

}
