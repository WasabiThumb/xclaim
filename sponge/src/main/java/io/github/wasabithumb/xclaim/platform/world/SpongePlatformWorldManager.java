package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;

import java.util.*;

public class SpongePlatformWorldManager implements PlatformWorldManager {

    private final SpongePlatform platform;
    private final WorldManager handle;

    public SpongePlatformWorldManager(@NotNull SpongePlatform platform) {
        this.platform = platform;
        this.handle = platform.server().worldManager();
    }

    @Override
    public @NotNull List<PlatformWorld> getAll() {
        Collection<ServerWorld> collection = this.handle.worlds();
        List<PlatformWorld> list = new ArrayList<>(collection.size());
        for (ServerWorld sw : collection) {
            list.add(new SpongePlatformWorld(this.platform, sw));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public @Nullable SpongePlatformWorld getWorld(@NotNull UUID uuid) {
        return this.handle.worldKey(uuid)
                .flatMap(this.handle::world)
                .map((ServerWorld sw) -> new SpongePlatformWorld(this.platform, sw))
                .orElse(null);
    }

    @Override
    public @Nullable SpongePlatformWorld getWorld(@NotNull String name) {
        ResourceKey rk = ResourceKey.resolve(name);
        return this.handle.world(rk)
                .map((ServerWorld sw) -> new SpongePlatformWorld(this.platform, sw))
                .orElse(null);
    }

}
