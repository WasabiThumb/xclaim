package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Barrel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitPlatformBlock implements PlatformBlock {

    protected final BukkitPlatform platform;
    protected final Block handle;

    public BukkitPlatformBlock(@NotNull BukkitPlatform platform, @NotNull Block handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull Block handle() {
        return this.handle;
    }

    @Override
    public @NotNull BukkitPlatformWorld world() {
        return new BukkitPlatformWorld(this.platform, this.handle.getWorld());
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

    @Override
    public boolean isRedstoneComponent() {
        BlockData bd = this.handle().getBlockData();
        return bd instanceof Powerable || bd instanceof AnaloguePowerable;
    }

    @Override
    public boolean isDoor() {
        BlockData bd = this.handle().getBlockData();
        return bd instanceof Openable && !(bd instanceof Barrel);
    }

    @Override
    public boolean canWaterlog() {
        BlockData bd = this.handle().getBlockData();
        return (bd instanceof Waterlogged wl) && !wl.isWaterlogged();
    }

    @Override
    public boolean isWaterlogged() {
        BlockData bd = this.handle().getBlockData();
        return (bd instanceof Waterlogged wl) && wl.isWaterlogged();
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof BukkitPlatformBlock other) {
            return Objects.equals(this.handle, other.handle);
        }
        return super.equals(obj);
    }

}
