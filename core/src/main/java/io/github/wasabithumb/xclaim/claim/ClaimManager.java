package io.github.wasabithumb.xclaim.claim;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcement;
import io.github.wasabithumb.xclaim.config.struct.sub.RulesConfig;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.integration.Integrations;
import io.github.wasabithumb.xclaim.integration.map.MapIntegration;
import io.github.wasabithumb.xclaim.integration.map.MapOperation;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClaimManager {

    private final XClaim runtime;
    private final ClaimDataManager data;
    private final ClaimEnforcement enforcement;
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Map<String, Claim> byName = new HashMap<>();
    private final ReadWriteLock byNameLock = new ReentrantReadWriteLock();
    private final Map<UUID, Set<Claim>> byOwner = new HashMap<>();
    private final ReadWriteLock byOwnerLock = new ReentrantReadWriteLock();
    private final Map<Long, Set<Claim>> byRegion = new HashMap<>();
    private final ReadWriteLock byRegionLock = new ReentrantReadWriteLock();

    public ClaimManager(@NotNull XClaim runtime, @NotNull ClaimDataManager data) {
        this.runtime = runtime;
        this.data = data;
        this.enforcement = new ClaimEnforcement(this);
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
        final Long region = BitManipulation.i32i64(reference.x >> 3, reference.z >> 3);
        this.byRegionLock.readLock().lock();
        try {
            Set<Claim> candidates = this.byRegion.get(region);
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
        } finally {
            this.byRegionLock.readLock().unlock();
        }
    }

    public @Nullable Claim getByChunk(@NotNull PlatformChunk chunk) {
        return this.getByChunk(ChunkReference.of(chunk));
    }

    public @NotNull List<Claim> getAll() {
        this.byNameLock.readLock().lock();
        try {
            return List.copyOf(this.byName.values());
        } finally {
            this.byNameLock.readLock().unlock();
        }
    }

    public @NotNull Set<Claim> getByOwner(@NotNull PlatformUser user) {
        this.byOwnerLock.readLock().lock();
        try {
            Set<Claim> set = this.byOwner.get(user.uuid());
            if (set == null) return Collections.emptySet();
            return Set.copyOf(set);
        } finally {
            this.byOwnerLock.readLock().unlock();
        }
    }

    /**
     * Creates a new claim with an automatically generated unused name.
     * @param user The owner of the claim; if null, the console user is used.
     * @param firstChunk First chunk of the claim.
     * @param silent If true, no status messages will be sent to the user
     * @return The newly created claim, or null if failed for any reason (reason messages will be sent if silent is false)
     */
    public @Nullable Claim create(@Nullable PlatformUser user, @NotNull ChunkReference firstChunk, boolean silent) {
        if (user == null) user = this.runtime.platform().users().console();
        if (!this.runtime.rootConfig().worlds().checkLists(firstChunk.world)) {
            if (!silent) user.sendMessage(this.runtime.lang("gui-new-disallowed"));
            return null;
        }

        RulesConfig rules = this.runtime.rootConfig().rules();
        int maxClaims = rules.maxClaims(user);
        int maxClaimsInWorld = rules.maxClaimsInWorld(user);
        int curClaims = 0;
        int curClaimsInWorld = 0;
        for (Claim c : this.getByOwner(user)) {
            curClaims++;
            if (c.world().uuid().equals(firstChunk.world.uuid())) curClaimsInWorld++;
        }
        if ((maxClaims >= 0 && curClaims >= maxClaims) ||
                (maxClaimsInWorld >= 0 && curClaimsInWorld >= maxClaimsInWorld)
        ) {
            if (!silent) user.sendMessage(this.runtime.lang("gui-new-max-claims"));
            return null;
        }

        ClaimData cd = this.data.create(this.nextClaimName(), user.uuid(), firstChunk.world);
        boolean success = false;
        try {
            Claim c = new Claim(this, cd, new ClaimState());
            success = c.modifyChunks(user)
                    .silent(silent)
                    .ignorePlacementRules(true)
                    .addChunk(firstChunk)
                    .commit()
                    .isSuccess();
            return success ? c : null;
        } finally {
            if (!success) this.data.queueDrop(cd);
        }
    }

    /**
     * Creates a new claim with an automatically generated unused name.
     * @param user The owner of the claim; if null, the console user is used.
     * @param firstChunk First chunk of the claim.
     * @param silent If true, no status messages will be sent to the user
     * @return The newly created claim, or null if failed for any reason (reason messages will be sent if silent is false)
     * @see #create(PlatformUser, ChunkReference, boolean)
     */
    public @Nullable Claim create(@Nullable PlatformUser user, @NotNull PlatformChunk firstChunk, boolean silent) {
        return this.create(user, ChunkReference.of(firstChunk), silent);
    }

    /**
     * Creates a new claim with an automatically generated unused name.
     * @param user The owner of the claim; if null, the console user is used.
     * @param firstChunk First chunk of the claim.
     * @return The newly created claim, or null if failed for any reason (messages will be sent)
     * @see #create(PlatformUser, ChunkReference, boolean)
     */
    public @Nullable Claim create(@Nullable PlatformUser user, @NotNull ChunkReference firstChunk) {
        return this.create(user, firstChunk, false);
    }

    /**
     * Creates a new claim with an automatically generated unused name.
     * @param user The owner of the claim; if null, the console user is used.
     * @param firstChunk First chunk of the claim.
     * @return The newly created claim, or null if failed for any reason (messages will be sent)
     * @see #create(PlatformUser, ChunkReference)
     */
    public @Nullable Claim create(@Nullable PlatformUser user, @NotNull PlatformChunk firstChunk) {
        return this.create(user, ChunkReference.of(firstChunk));
    }

    private @NotNull String nextClaimName() {
        String root = this.runtime.lang("new-claim") + " #";
        int rootLen = root.length();
        StringBuilder sb = new StringBuilder(root);
        String ret;

        synchronized (this.idCounter) {
            do {
                sb.setLength(rootLen);
                sb.append(this.idCounter.getAndIncrement());
                ret = sb.toString();
            } while (this.getByName(ret) != null);
        }

        return ret;
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
            this.commit0(new Claim(this, data, state), true);
        }
    }

    /**
     * Should be called any time a Claim is updated.
     */
    @ApiStatus.Internal
    public void commit(@NotNull Claim claim) {
        this.commit0(claim, false);
    }

    private void commit0(@NotNull Claim claim, boolean initial) {
        ClaimData data = claim.data();
        String name = data.getName();
        UUID owner = data.getOwner();
        Set<Long> chunks = data.getChunks();
        boolean updateChunks = initial || data.didUpdateChunks();

        ClaimState state = claim.state();
        state.lock.lock();
        try {
            if (data.getWorld().resolve(this.runtime.platform().worlds()) == null || chunks.isEmpty()) {
                this.data.queueDrop(data);
                this.drop(claim, state);
                return;
            }
            this.data.queueSync(data);
            this.update(claim, state, name, owner, chunks, updateChunks);
        } finally {
            state.lock.unlock();
        }
    }

    private void update(
            @NotNull Claim claim,
            @NotNull ClaimState state,
            @NotNull String name,
            @NotNull UUID owner,
            @NotNull Set<Long> chunks,
            boolean updateChunks
    ) {
        this.enforcement.setEnabled(true);

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

        if (!owner.equals(state.attachedOwner)) {
            this.byOwnerLock.writeLock().lock();
            try {
                if (state.attachedOwner != null) {
                    Set<Claim> old = this.byOwner.get(state.attachedOwner);
                    if (old != null && old.remove(claim) && old.isEmpty()) this.byOwner.remove(state.attachedOwner);
                }
                this.byOwner.computeIfAbsent(owner, (UUID ignored) -> new HashSet<>()).add(claim);
            } finally {
                this.byOwnerLock.writeLock().unlock();
            }
            state.attachedOwner = owner;
        }

        if (!updateChunks) return;

        Set<Long> old = state.attachedRegions;
        Set<Long> cur = new HashSet<>(old.size());
        state.attachedRegions = cur;

        int[] tmp;
        for (Long chunk : chunks) {
            tmp = BitManipulation.i64i32(chunk);
            cur.add(BitManipulation.i32i64(tmp[0] >> 3, tmp[1] >> 3));
        }

        List<Claim> causedUpdatesFor = new LinkedList<>();
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
                for (Claim neighbor : set) {
                    if (neighbor.equals(claim)) continue;
                    if (!claim.data().getWorld().matches(neighbor.world())) continue;
                    if (neighbor.data().removeChunks(chunks)) {
                        causedUpdatesFor.add(neighbor);
                    }
                }
                set.add(claim);
            }
        } finally {
            this.byRegionLock.writeLock().unlock();
        }

        for (Claim c : causedUpdatesFor)
            this.commit(c);

        Integrations integrations = this.runtime.integrations();
        if (!integrations.hasMap()) return;
        MapIntegration map = integrations.map();
        map.queueOperation(MapOperation.update(claim));
    }

    private void drop(@NotNull Claim claim, @NotNull ClaimState state) {
        Integrations integrations = this.runtime.integrations();
        if (integrations.hasMap()) {
            MapIntegration map = integrations.map();
            map.queueOperation(MapOperation.delete(claim));
        }

        boolean empty;
        this.byNameLock.writeLock().lock();
        try {
            Claim removed = this.byName.remove(state.attachedName);
            if (!claim.equals(removed))
                this.byName.put(state.attachedName, claim);
            state.attachedName = null;
            empty = this.byName.isEmpty();
        } finally {
            this.byNameLock.writeLock().unlock();
        }
        if (empty) this.enforcement.setEnabled(false);

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

    public void close() throws Exception {
        this.data.close();
    }

}
