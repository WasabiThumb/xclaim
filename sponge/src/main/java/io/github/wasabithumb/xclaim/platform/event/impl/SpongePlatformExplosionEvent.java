package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.SpongePlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class SpongePlatformExplosionEvent
        extends SpongePlatformEvent<ExplosionEvent.Detonate>
        implements PlatformExplosionEvent
{

    @Adapter
    public SpongePlatformExplosionEvent(
            @NotNull SpongePlatform platform,
            @NotNull ExplosionEvent.Detonate handle
    ) {
        super(platform, handle);
    }

    //

    @Override
    public @Nullable PlatformEntity entity() {
        SpongePlatformTypeAdapter adapter = this.platform.adapter();

        Optional<Living> igniter = this.handle.context().get(EventContextKeys.IGNITER);
        if (igniter.isPresent()) {
            return adapter.entity(igniter);
        }

        return this.handle.cause()
                .first(Entity.class)
                .map(adapter::entity)
                .orElse(null);
    }

    @Override
    public @NotNull List<PlatformBlock> blocks() {
        return new BlockList(this);
    }

    //

    private static final class BlockList extends AbstractList<PlatformBlock> {

        private final SpongePlatformExplosionEvent event;
        private List<ServerLocation> backing;

        BlockList(@NotNull SpongePlatformExplosionEvent event) {
            this.event = event;
            this.updateBacking();
        }

        //

        private void updateBacking() {
            this.backing = this.event.handle.affectedLocations();
        }

        private @NotNull SpongePlatformBlock adapt(@NotNull ServerLocation sl) {
            return new SpongePlatformBlock(this.event.platform, sl.world(), sl.blockX(), sl.blockY(), sl.blockZ());
        }

        //

        @Override
        public @NotNull PlatformBlock get(int i) {
            return this.adapt(this.backing.get(i));
        }

        @Override
        public int size() {
            return this.backing.size();
        }

        @Override
        public void clear() {
            this.backing = new ArrayList<>(0);
            this.event.handle.filterAffectedLocations((ServerLocation ignored) -> false);
        }

        @Override
        public @NotNull PlatformBlock remove(int index) {
            ServerLocation sl = this.backing.get(index);
            this.removeServerLocations(Collections.singleton(sl));
            return this.adapt(sl);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof PlatformBlock pb)) return false;
            return this.removeBlocks(Collections.singleton(pb));
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            int max = c.size();
            int len = 0;
            PlatformBlock[] arr = new PlatformBlock[max];

            for (Object value : c) {
                if (!(value instanceof PlatformBlock pb)) continue;
                if (len == max) throw new ConcurrentModificationException();
                arr[len++] = pb;
            }

            if (len == 0) return false;
            if (len < max) {
                PlatformBlock[] cpy = new PlatformBlock[len];
                System.arraycopy(arr, 0, cpy, 0, len);
                arr = cpy;
            }

            return this.removeBlocks(Arrays.asList(arr));
        }

        //

        private boolean removeBlocks(@NotNull Iterable<PlatformBlock> blocks) {
            return this.removeLocations(
                    blocks,
                    (PlatformBlock b) -> b.world().uuid(),
                    PlatformBlock::x,
                    PlatformBlock::y,
                    PlatformBlock::z
            );
        }

        private void removeServerLocations(@NotNull Iterable<ServerLocation> serverLocations) {
            this.removeLocations(
                    serverLocations,
                    (ServerLocation sl) -> sl.world().uniqueId(),
                    ServerLocation::blockX,
                    ServerLocation::blockY,
                    ServerLocation::blockZ
            );
        }

        private <Q> boolean removeLocations(
                final @NotNull Iterable<Q> locations,
                final @NotNull Function<Q, UUID> getLocationID,
                final @NotNull ToIntFunction<Q> getLocationX,
                final @NotNull ToIntFunction<Q> getLocationY,
                final @NotNull ToIntFunction<Q> getLocationZ
        ) {
            final int oldSize = this.size();
            this.event.handle.filterAffectedLocations((ServerLocation sl) -> {
                for (Q loc : locations) {
                    if (!sl.world().uniqueId().equals(getLocationID.apply(loc))) continue;
                    if (sl.blockX() != getLocationX.applyAsInt(loc)) continue;
                    if (sl.blockY() != getLocationY.applyAsInt(loc)) continue;
                    if (sl.blockZ() != getLocationZ.applyAsInt(loc)) continue;
                    return false;
                }
                return true;
            });
            this.updateBacking();
            return this.size() != oldSize;
        }

    }

}
