package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.BukkitPlatformPersistentDataContainer;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class BukkitPlatformWorld implements PlatformWorld {

    protected final BukkitPlatform platform;
    protected final UUID uuid;
    protected final Reference<World> handleRef;
    public BukkitPlatformWorld(@NotNull BukkitPlatform platform, @NotNull World handle) {
        this.platform = platform;
        this.uuid = handle.getUID();
        this.handleRef = new WeakReference<>(handle);
    }

    @Override
    public boolean isValid() {
        return this.handleRef.get() != null;
    }

    @Override
    public @NotNull World handle() {
        World ret = this.handleRef.get();
        if (ret == null)
            throw new IllegalStateException("World (" + this.uuid + ") is unloaded");
        return ret;
    }

    @Override
    public @NotNull String name() {
        return this.handle().getName();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.uuid;
    }

    @Override
    public @NotNull BukkitPlatformChunk getChunk(int x, int z) {
        return new BukkitPlatformChunk(this.platform, this.handle().getChunkAt(x, z));
    }

    @Override
    public @NotNull BukkitPlatformBlock getBlock(int x, int y, int z) {
        return new BukkitPlatformBlock(this.platform, this.handle().getBlockAt(x, y, z));
    }

    @Override
    public int getMaxHeight() {
        return this.handle().getMaxHeight();
    }

    @Override
    public int getMinHeight() {
        return this.handle().getMinHeight();
    }

    @Override
    public boolean keepInventory() {
        return Boolean.TRUE.equals(this.handle().getGameRuleValue(GameRule.KEEP_INVENTORY));
    }

    @Override
    public void createExplosion(@NotNull PlatformLocation location, int power, boolean setFire, boolean breakBlocks) {
        this.handle().createExplosion(
                new Location(this.handle(), location.x(), location.y(), location.z()),
                power,
                setFire,
                breakBlocks
        );
    }

    @Override
    public @NotNull BukkitPlatformPersistentDataContainer pdc() {
        return new BukkitPlatformPersistentDataContainer(this.platform, this.handle().getPersistentDataContainer());
    }

}
