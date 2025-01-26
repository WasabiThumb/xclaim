package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.SpongePlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.Entity;

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

    // TODO: Lots

    @Override
    public @NotNull PlatformLocation location() {
        return null;
    }

    @Override
    public void teleport(@NotNull PlatformLocation location) {
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

    @Override
    public boolean isExplosive() {
        return false;
    }

    @Override
    public @Nullable PlatformPlayer sourcePlayer() {
        return null;
    }

    @Override
    public boolean isInGroup(@NotNull PlatformEntityGroup group) {
        return false;
    }

    @Override
    public void damage(double damage) {

    }

    @Override
    public void addVelocity(double x, double y, double z) {

    }

    @Override
    public @Nullable PlatformEntity target() {
        return null;
    }

    @Override
    public boolean isPoweredCreeper() {
        return false;
    }

    @Override
    public void remove() {

    }

}
