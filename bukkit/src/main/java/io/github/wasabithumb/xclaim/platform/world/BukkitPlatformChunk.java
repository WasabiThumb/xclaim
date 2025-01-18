package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformChunk implements PlatformChunk {

    protected final BukkitPlatform platform;
    protected final Chunk handle;

    public BukkitPlatformChunk(@NotNull BukkitPlatform platform, @NotNull Chunk handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull Chunk handle() {
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
    public int z() {
        return this.handle.getZ();
    }

    @Override
    public boolean isLoaded() {
        return this.handle.isLoaded();
    }
}
