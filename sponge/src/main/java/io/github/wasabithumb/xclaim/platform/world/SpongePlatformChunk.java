package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.server.ServerWorld;

public class SpongePlatformChunk implements PlatformChunk {

    private final SpongePlatform platform;
    private final WorldChunk handle;

    public SpongePlatformChunk(
            @NotNull SpongePlatform platform,
            @NotNull WorldChunk handle
    ) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull WorldChunk handle() {
        return this.handle;
    }

    @Override
    public @NotNull PlatformWorld world() {
        World<?, ?> w = this.handle.world();
        if (!(w instanceof ServerWorld sw)) {
            throw new IllegalStateException("Chunk world is not of the server");
        }
        return new SpongePlatformWorld(this.platform, sw);
    }

    @Override
    public int x() {
        return this.handle.chunkPosition().x();
    }

    @Override
    public int z() {
        return this.handle.chunkPosition().z();
    }

    @Override
    public boolean isLoaded() {
        return !this.handle.isEmpty();
    }

}
