package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ModifyChunksClaimTransaction extends ClaimTransaction {

    private final Set<Long> additions = new HashSet<>();
    private final Set<Long> removals = new HashSet<>();
    public ModifyChunksClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    @Override
    protected void onCommit() {
        for (Long addition : this.additions) this.data.addChunk(addition);
        for (Long removal : this.removals) this.data.removeChunk(removal);
    }

    private boolean update(long token, boolean add) {
        boolean ret = false;
        Set<Long> a = add ? this.additions : this.removals;
        Set<Long> b = add ? this.removals : this.additions;
        if (a.add(token)) ret = true;
        if (b.remove(token)) ret = true;
        return ret;
    }

    @Contract("_ -> this")
    public @NotNull ModifyChunksClaimTransaction addChunk(@NotNull ChunkReference cr) {
        if (this.manageCheck()) return this;
        if (!this.data.getWorld().matches(cr.world)) {
            this.langMessage("chunk-editor-wrong-world");
            this.valid = false;
            return this;
        }
        // TODO: Definitely missing some checks that were present before, do that!
        long token = BitManipulation.i32i64(cr.x, cr.z);
        if (this.update(token, true)) {
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
        if (!this.data.getWorld().matches(cr.world)) return this;
        long token = BitManipulation.i32i64(cr.x, cr.z);
        if (this.update(token, false)) {
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

}
