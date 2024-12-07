package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformBlock implements PlatformBlock {

    protected final Block handle;
    public BukkitPlatformBlock(@NotNull Block handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull Block handle() {
        return this.handle;
    }

    @Override
    public @NotNull BukkitPlatformWorld world() {
        return new BukkitPlatformWorld(this.handle.getWorld());
    }

    @Override
    public int x() {
        return this.handle.getX();
    }

    @Override
    public int y() {
        return this.handle.getY();
    }

    @Override
    public int z() {
        return this.handle.getZ();
    }

    @Override
    public @NotNull PlatformMaterial getType() {
        return BukkitPlatformMaterial.of(this.handle.getType());
    }

    @Override
    public void setType(@NotNull PlatformMaterial type) {
        this.handle.setType(BukkitPlatformMaterial.adapt(type));
    }

    @Override
    public boolean isContainer() {
        return this.handle.getState() instanceof Container;
    }

}
