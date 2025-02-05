package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.SpongePlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformWorld;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityCategories;
import org.spongepowered.api.entity.EntityCategory;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.vehicle.Vehicle;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.projectile.source.ProjectileSource;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.UUID;

public class SpongePlatformEntity implements PlatformEntity {

    protected final SpongePlatform platform;
    protected final Entity handle;

    public SpongePlatformEntity(@NotNull SpongePlatform platform, @NotNull Entity handle) {
        this.platform = platform;
        this.handle = handle;
    }

    @Override
    public @NotNull Entity handle() {
        return this.handle;
    }

    @Override
    public @NotNull UUID uuid() {
        return this.handle.uniqueId();
    }

    @Override
    public @NotNull PlatformEntityType type() {
        return SpongePlatformEntityType.of(this.handle.type());
    }

    @Override
    public @NotNull SpongePlatformPersistentDataContainer pdc() {
        return new SpongePlatformPersistentDataContainer(this.platform, this.handle);
    }

    @Override
    public @NotNull PlatformLocation location() {
        ServerLocation sl = this.handle.serverLocation();
        Vector3d rotation = this.handle.rotation();
        return new PlatformLocation(
                new SpongePlatformWorld(this.platform, sl.world()),
                sl.x(), sl.y(), sl.z(),
                (float) rotation.y(), (float) rotation.x()
        );
    }

    @Override
    public void teleport(@NotNull PlatformLocation location) {
        SpongePlatformWorld spw = (SpongePlatformWorld) location.world();
        ServerWorld sw = spw.handle();
        this.handle.setLocationAndRotation(
                sw.location(location.x(), location.y(), location.z()),
                new Vector3d(location.pitch(), location.yaw(), 0d)
        );
    }

    @Override
    public boolean isProjectile() {
        return this.handle instanceof Projectile;
    }

    @Override
    public boolean isExplosive() {
        return this.handle instanceof Explosive;
    }

    @Override
    public @Nullable PlatformPlayer sourcePlayer() {
        if (this.handle instanceof Projectile p) {
            ProjectileSource ps = p.shooter()
                    .map(Value.Mutable::get)
                    .orElse(null);
            if (ps instanceof ServerPlayer sp) {
                return new SpongePlatformPlayer(this.platform, sp);
            }
        }
        // TODO: Try handling TNT & end crystals?
        return null;
    }

    @Override
    public boolean isInGroup(@NotNull PlatformEntityGroup group) {
        EntityType<?> type = this.handle.type();
        EntityCategory category = type.category();

        if (RegistryUtil.referenceEquals(RegistryTypes.ENTITY_CATEGORY, category, EntityCategories.CREATURE) ||
                RegistryUtil.referenceEquals(RegistryTypes.ENTITY_CATEGORY, category, EntityCategories.WATER_CREATURE)
        ) {
            return group == PlatformEntityGroup.FRIENDLY;
        }

        if (RegistryUtil.referenceEquals(RegistryTypes.ENTITY_CATEGORY, category, EntityCategories.MONSTER)) {
            return group == PlatformEntityGroup.HOSTILE;
        }

        if (this.handle instanceof Vehicle)
            return group == PlatformEntityGroup.VEHICLE;

        if (this.handle instanceof ArmorStand || !(this.handle instanceof Living))
            return group == PlatformEntityGroup.NOT_ALIVE;

        return group == PlatformEntityGroup.MISC;
    }

    @Override
    public void damage(double damage) {
        this.handle.damage(damage, DamageSources.GENERIC);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        Value.Mutable<Vector3d> v = this.handle.velocity();
        v.set(v.get().add(x, y, z));
    }

    @Override
    public @Nullable PlatformEntity target() {
        return this.handle.get(Keys.TARGET_ENTITY)
                .map((Entity e) -> new SpongePlatformEntity(this.platform, e))
                .orElse(null);
    }

    @Override
    public boolean isPoweredCreeper() {
        return this.handle.get(Keys.IS_CHARGED).orElse(false);
    }

    @Override
    public void remove() {
        this.handle.remove();
    }

}
