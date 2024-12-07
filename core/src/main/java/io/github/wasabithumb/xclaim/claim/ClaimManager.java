package io.github.wasabithumb.xclaim.claim;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClaimManager implements AutoCloseable {

    private final XClaim runtime;
    private final ClaimDataManager data;
    private final Map<String, Claim> byName = new HashMap<>();
    private final ReadWriteLock byNameLock = new ReentrantReadWriteLock();
    private final Map<Long, Set<Claim>> byRegion = new HashMap<>();
    private final ReadWriteLock byRegionLock = new ReentrantReadWriteLock();

    public ClaimManager(@NotNull XClaim runtime, @NotNull ClaimDataManager data) {
        this.runtime = runtime;
        this.data = data;
    }

    /**
     * Returns the XClaim instance that spawned this manager.
     */
    public @NotNull XClaim runtime() {
        return this.runtime;
    }

    public @Nullable Claim getByName(@NotNull String name) {
        name = name.toLowerCase(Locale.ROOT);
        this.byNameLock.readLock().lock();
        try {
            return this.byName.get(name);
        } finally {
            this.byNameLock.readLock().unlock();
        }
    }

    public @Nullable Claim getByChunk(@NotNull ChunkReference reference) {
        final Long region = BitManipulation.i32i64(reference.x >> 5, reference.z >> 5);
        Set<Claim> candidates;
        this.byRegionLock.readLock().lock();
        try {
            candidates = this.byRegion.get(region);
        } finally {
            this.byRegionLock.readLock().unlock();
        }
        if (candidates == null) return null;
        for (Claim candidate : candidates) {
            if (!candidate.data().getWorld().matches(reference.world)) continue;
            int[] tmp;
            for (Long chunk : candidate.data().getChunks()) {
                tmp = BitManipulation.i64i32(chunk);
                if (tmp[0] == reference.x && tmp[1] == reference.z) return candidate;
            }
        }
        return null;
    }

    public @Nullable Claim getByChunk(@NotNull PlatformChunk chunk) {
        return this.getByChunk(ChunkReference.of(chunk));
    }

    @ApiStatus.Internal
    public void load() {
        final Logger logger = this.runtime.logger();
        final Lang lang = this.runtime.lang();
        logger.log(Level.INFO, lang.get("claims-load"));
        try {
            this.load0();
        } catch (Exception e) {
            logger.log(Level.WARNING, lang.get("claims-load-err"), e);
        }
    }

    private void load0() {
        final Set<ClaimData.Token> tokens = this.data.keys();
        ClaimData data;
        ClaimState state;
        for (ClaimData.Token token : tokens) {
            data = this.data.load(token);
            if (data == null) {
                throw new NullPointerException(token.toString());
            }
            state = new ClaimState();
            this.commit(new Claim(this, data, state));
        }
    }

    /**
     * Should be called any time a Claim is updated.
     */
    @ApiStatus.Internal
    public void commit(@NotNull Claim claim) {
        ClaimData data = claim.data();
        String name = data.getName();
        Set<Long> chunks = data.getChunks();
        boolean updateChunks = data.didUpdateChunks();

        ClaimState state = claim.state();
        state.lock.lock();
        try {
            if (data.getWorld().resolve(this.runtime.platform().worlds()) == null || chunks.isEmpty()) {
                this.data.queueDrop(data);
                this.drop(claim, state);
                return;
            }
            this.data.queueSync(data);
            this.update(claim, state, name, chunks, updateChunks);
        } finally {
            state.lock.unlock();
        }
    }

    private void update(@NotNull Claim claim, @NotNull ClaimState state, @NotNull String name, @NotNull Set<Long> chunks, boolean updateChunks) {
        name = name.toLowerCase(Locale.ROOT);
        if (!name.equals(state.attachedName)) {
            this.byNameLock.writeLock().lock();
            try {
                if (state.attachedName != null) this.byName.remove(state.attachedName);
                this.byName.put(name, claim);
            } finally {
                this.byNameLock.writeLock().unlock();
            }
            state.attachedName = name;
        }

        if (!updateChunks) return;

        Set<Long> old = state.attachedRegions;
        Set<Long> cur = new HashSet<>(old.size());
        state.attachedRegions = cur;

        int[] tmp;
        for (Long chunk : chunks) {
            tmp = BitManipulation.i64i32(chunk);
            cur.add(BitManipulation.i32i64(tmp[0] >> 5, tmp[1] >> 5));
        }

        this.byRegionLock.writeLock().lock();
        try {
            Set<Claim> set;

            for (Long region : old) {
                if (cur.contains(region)) continue;
                set = this.byRegion.get(region);
                if (set == null) continue;
                if (set.remove(claim) && set.isEmpty()) {
                    this.byRegion.remove(region);
                }
            }

            for (Long region : cur) {
                if (old.contains(region)) continue;
                set = this.byRegion.computeIfAbsent(region, (Long ignored) -> new HashSet<>());
                set.add(claim);
            }
        } finally {
            this.byRegionLock.writeLock().unlock();
        }
    }

    private void drop(@NotNull Claim claim, @NotNull ClaimState state) {
        this.byNameLock.writeLock().lock();
        try {
            Claim removed = this.byName.remove(state.attachedName);
            if (!claim.equals(removed))
                this.byName.put(state.attachedName, claim);
            state.attachedName = null;
        } finally {
            this.byNameLock.writeLock().unlock();
        }

        if (state.attachedRegions.isEmpty()) return;

        this.byRegionLock.writeLock().lock();
        try {
            Set<Claim> set;
            for (Long region : state.attachedRegions) {
                set = this.byRegion.get(region);
                if (set == null) continue;
                set.remove(claim);
            }
        } finally {
            this.byRegionLock.writeLock().unlock();
        }
        state.attachedRegions = Collections.emptySet();
    }

    @Override
    public void close() throws Exception {
        this.data.close();
    }

}
