package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.config.struct.sub.RulesConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.EconomyConfig;
import io.github.wasabithumb.xclaim.integration.Integrations;
import io.github.wasabithumb.xclaim.integration.economy.EconomyIntegration;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionIntegration;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionPermission;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionRegion;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ModifyChunksClaimTransaction extends ClaimTransaction {

    private final Set<Long> additions = new HashSet<>();
    private final Set<Long> removals = new HashSet<>();
    private int price = 0;
    private boolean ignorePlacementRules = false;
    private boolean allowDeletion = true;
    public ModifyChunksClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    @Override
    protected void onCommit() {
        if (!this.flushBalance()) {
            this.valid = false;
            return;
        }
        for (Long addition : this.additions) this.data.addChunk(addition);
        for (Long removal : this.removals) this.data.removeChunk(removal);
    }

    private boolean update(int x, int z, boolean add) {
        if (add == this.claim.containsChunk(x, z)) return false;
        long token = BitManipulation.i32i64(x, z);
        boolean ret = false;
        Set<Long> a = add ? this.additions : this.removals;
        Set<Long> b = add ? this.removals : this.additions;
        if (a.add(token)) ret = true;
        if (b.remove(token)) ret = true;
        return ret;
    }

    @ApiStatus.Experimental
    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction ignorePlacementRules(boolean ignore) {
        this.ignorePlacementRules = ignore;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction allowDeletion(boolean allowDeletion) {
        this.allowDeletion = allowDeletion;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction addChunk(@NotNull ChunkReference cr) {
        if (this.manageCheck()) return this;
        if (!this.data.getWorld().matches(cr.world)) {
            this.langMessage("chunk-editor-wrong-world");
            this.valid = false;
            return this;
        }
        if (this.hasProtectionConflict(cr)) {
            this.langMessage("chunk-editor-protection-deny");
            this.valid = false;
            return this;
        }
        if (this.hasOverrideConflict(cr)) {
            this.langMessage("chunk-editor-taken");
            this.valid = false;
            return this;
        }
        if (this.hasChunkLimitConflict()) {
            this.langMessage("chunk-editor-max");
            this.valid = false;
            return this;
        }
        if (this.hasDistanceConflict(cr)) {
            this.langMessage("chunk-editor-min-distance-deny");
            this.valid = false;
            return this;
        }
        if (this.hasPlacementConflict(cr)) {
            this.langMessage("chunk-editor-adjacent");
            this.valid = false;
            return this;
        }
        if (this.update(cr.x, cr.z, true)) {
            if (this.hasPoorConflict()) {
                this.langMessage("chunk-editor-cant-afford");
                this.valid = false;
                return this;
            }
            this.langMessage("chunk-editor-add", Integer.toString(cr.x), Integer.toString(cr.z));
        } else {
            this.langMessage("chunk-editor-redundant-add");
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction addChunk(@NotNull PlatformChunk chunk) {
        return this.addChunk(ChunkReference.of(chunk));
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction removeChunk(@NotNull ChunkReference cr) {
        if (this.manageCheck()) return this;
        if (this.data.getWorld().matches(cr.world) && this.update(cr.x, cr.z, false)) {
            if (this.isDeleting() && (!this.allowDeletion || !this.checkPermission(Permission.DELETE))) {
                this.langMessage("permHandler-stdError");
                this.valid = false;
                return this;
            }
            this.subtractUnclaimReward();
            this.langMessage("chunk-editor-remove");
        } else {
            this.langMessage("chunk-editor-redundant-remove");
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction removeChunk(@NotNull PlatformChunk chunk) {
        return this.removeChunk(ChunkReference.of(chunk));
    }

    public boolean isDeleting() {
        return this.effectiveChunkCount(false) == 0;
    }

    @Contract(" -> this")
    public @NotNull ModifyChunksClaimTransaction clear() {
        if (this.manageCheck()) return this;
        for (Long token : this.data.getChunks()) {
            if (this.removals.contains(token)) continue;
            this.subtractUnclaimReward();
            this.removals.add(token);
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    //

    private int effectiveChunkCount(boolean all) {
        int curChunks = this.data.getChunkCount() + this.additions.size() - this.removals.size();
        if (all) {
            for (Claim c : this.manager.getByOwner(this.user)) {
                if (c.equals(this.claim)) continue;
                curChunks += c.chunkCount();
            }
        }
        return curChunks;
    }

    private int effectiveChunkCount() {
        return this.effectiveChunkCount(true);
    }

    private boolean hasProtectionConflict(@NotNull ChunkReference cr) {
        Integrations integrations = this.runtime.integrations();
        if (!integrations.hasProtection()) return false;
        ProtectionIntegration protection = integrations.protection();

        Collection<ProtectionRegion> regions = protection.getRegionsAt(cr);
        for (ProtectionRegion region : regions) {
            if (this.hasProtectionConflict0(region)) return true;
        }

        return false;
    }

    private boolean hasProtectionConflict0(@NotNull ProtectionRegion region) {
        Set<ProtectionPermission> permissions = region.getPermissions(this.user);
        for (ProtectionPermission perm : ProtectionPermission.values()) {
            if (!permissions.contains(perm)) return true;
        }
        return false;
    }

    private boolean hasOverrideConflict(@NotNull ChunkReference cr) {
        Claim existing = this.manager.getByChunk(cr);
        if (existing == null) return false;
        if (existing.checkPermission(this.user, Permission.MANAGE)) return false;
        return !(this.user.isOp() || this.user.hasPermission("xclaim.override"));
    }

    private boolean hasChunkLimitConflict() {
        RulesConfig rules = this.runtime.rootConfig().rules();
        int maxChunks = rules.maxChunks(this.user);
        if (maxChunks < 0) return false;
        return this.effectiveChunkCount() >= maxChunks;
    }

    private boolean hasDistanceConflict(@NotNull ChunkReference cr) {
        int range = this.runtime.rootConfig().rules().minDistance();
        if (range < 1) return false;
        if (range > 16) {
            // TODO: Maybe generate a warning here? Checking over 256 chunks
            //       just to honor a (probably mistakenly) bad config seems dicey.
            range = 16;
        }

        int rangeSqr = range * range;
        int distSqr;
        Claim claim;
        for (int mX=-range; mX <= range; mX++) {
            for (int mZ=-range; mZ <= range; mZ++) {
                if (mX == 0 && mZ == 0) continue;
                distSqr = (mX * mX) + (mZ * mZ);
                if (distSqr > rangeSqr) continue;

                claim = this.manager.getByChunk(cr.getRelative(mX, mZ));
                if (claim == null) continue;
                if (claim.checkPermission(this.user, Permission.MANAGE)) continue;

                return true;
            }
        }

        return false;
    }

    private boolean hasPlacementConflict(@NotNull ChunkReference cr) {
        if (this.ignorePlacementRules) return false;
        RulesConfig.PlacementRule rule = this.runtime.rootConfig().rules().placement();
        if (rule == RulesConfig.PlacementRule.NONE) return false;
        Claim rel;

        rel = this.manager.getByChunk(cr.getRelative(-1, 0));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(1, 0));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(0, -1));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(0, 1));
        if (rel != null && rel.equals(this.claim)) return false;

        if (rule == RulesConfig.PlacementRule.CARDINAL) return true;

        rel = this.manager.getByChunk(cr.getRelative(-1, -1));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(-1, 1));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(1, 1));
        if (rel != null && rel.equals(this.claim)) return false;
        rel = this.manager.getByChunk(cr.getRelative(1, -1));

        return rel == null || !rel.equals(this.claim);
    }

    // awesome name
    private boolean hasPoorConflict() {
        Integrations integrations = this.runtime.integrations();
        if (!integrations.hasEconomy()) return false;

        EconomyIntegration eco = integrations.economy();
        EconomyConfig cfg = this.runtime.rootConfig().integrations().economy();

        int price = cfg.claimPrice(this.user);
        if (price == 0) return false;

        int freeChunks = cfg.freeChunks(this.user);
        if (freeChunks > 0 && freeChunks != Integer.MAX_VALUE) {
            if (this.effectiveChunkCount() < freeChunks) return false;
        }

        this.price += price;
        if (this.price <= 0d) return false;
        return !eco.canAfford(this.user, this.price);
    }

    private void subtractUnclaimReward() {
        if (!this.runtime.integrations().hasEconomy()) return;
        EconomyConfig cfg = this.runtime.rootConfig().integrations().economy();

        int reward = cfg.unclaimReward(this.user);
        if (reward == 0) return;

        int freeChunks = cfg.freeChunks(this.user);
        if (freeChunks == Integer.MAX_VALUE) return;
        if (freeChunks > 0 && this.effectiveChunkCount() <= freeChunks) return;

        this.price -= reward;
    }

    private boolean flushBalance() {
        int cmp = Integer.compare(this.price, 0);
        if (cmp == 0) return true;

        Integrations integrations = this.runtime.integrations();
        if (!integrations.hasEconomy()) return true;

        EconomyIntegration eco = integrations.economy();
        double price = this.price;
        if (cmp > 0) {
            if (eco.take(this.user, price)) {
                this.langMessage("chunk-editor-pay-success", eco.format(price));
                this.price = 0;
            } else {
                this.langMessage("chunk-editor-pay-fail", eco.format(price));
                return false;
            }
        } else {
            price = -price;
            eco.give(this.user, price);
            this.langMessage("chunk-editor-reward", eco.format(price));
            this.price = 0;
        }

        return true;
    }

}
