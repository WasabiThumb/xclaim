package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.gamerule.GameRules;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.UUID;

public class SpongePlatformWorld implements PlatformWorld {

    private final SpongePlatform platform;
    private final ServerWorld handle;

    public SpongePlatformWorld(@NotNull SpongePlatform platform, @NotNull ServerWorld handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull ServerWorld handle() {
        return this.handle;
    }

    @Override
    public boolean isValid() {
        return this.handle.isLoaded();
    }

    @Override
    public @NotNull UUID uuid() {
        return this.handle.uniqueId();
    }

    @Override
    public @NotNull String name() {
        ResourceKey rk = this.handle.key();
        if (ResourceKey.MINECRAFT_NAMESPACE.equals(rk.namespace())) return rk.value();
        return rk.formatted();
    }

    @Override
    public @NotNull PlatformChunk getChunk(int x, int z) {
        return new SpongePlatformChunk(this.platform, this.handle.chunk(x, 0, z));
    }

    @Override
    public @NotNull PlatformBlock getBlock(int x, int y, int z) {
        return new SpongePlatformBlock(this.platform, this.handle, x, y, z);
    }

    @Override
    public int getMinHeight() {
        return this.handle.min().y();
    }

    @Override
    public int getMaxHeight() {
        return this.handle.maximumHeight();
    }

    @Override
    public boolean keepInventory() {
        return this.handle.properties().gameRule(GameRules.KEEP_INVENTORY.get());
    }

    @Override
    public void createExplosion(@NotNull PlatformLocation location, int power, boolean setFire, boolean breakBlocks) {
        Explosion e = Explosion.builder()
                .resolution(power)
                .canCauseFire(setFire)
                .shouldBreakBlocks(breakBlocks)
                .build();

        this.handle.triggerExplosion(e);
    }

    @Override
    public @NotNull PlatformPersistentDataContainer pdc() {
        return this.platform.worldData().get(this.handle);
    }

}
