package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.BukkitPlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
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
        }
        return null;
    }

}
