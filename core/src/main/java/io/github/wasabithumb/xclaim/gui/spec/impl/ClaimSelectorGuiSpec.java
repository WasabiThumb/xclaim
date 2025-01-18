package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.helper.PaginatedGuiSpec;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import io.github.wasabithumb.xclaim.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ClaimSelectorGuiSpec extends PaginatedGuiSpec<Claim> {

    protected Collection<Claim> entries = null;
    private Comparator<Claim> sort = null;

    //

    @Override
    public @NotNull String layout() {
        return "claim-selector";
    }

    @Override
    protected synchronized @NotNull Collection<Claim> getEntries(@NotNull GuiInstance instance) {
        if (this.entries != null) return this.entries;

        final PlatformPlayer player = instance.player();
        final Collection<Claim> all = instance.runtime().claims().getAll();
        if (player.isOp()) return this.entries = new ArrayList<>(all);

        final int sizeEstimate = Math.max(
                Math.floorDiv(
                        all.size(),
                        instance.platform().users().playerCount() + 1
                ),
                8
        );
        final List<Claim> entries = new ArrayList<>(sizeEstimate);
        for (Claim c : all) {
            if (!this.canDisplay(c, player)) continue;
            entries.add(c);
        }

        return this.entries = entries;
    }

    protected boolean canDisplay(@NotNull Claim claim, @NotNull PlatformUser user)  {
        return claim.checkPermission(user, this.requiredPermission());
    }

    /** Not used if canDisplay is overriden */
    protected @NotNull Permission requiredPermission() {
        return Permission.MANAGE;
    }

    @Override
    protected synchronized final @NotNull Comparator<Claim> getSort(@NotNull GuiInstance instance) {
        if (this.sort != null) return this.sort;

        final PlatformPlayer player = instance.player();
        final UUID id = player.uuid();
        final ChunkReference cr = ChunkReference.of(player.location());

        // Default sort: Sort by distance (ascending), own claims first.
        return this.sort = Comparator.comparingLong((Claim c) -> {
            long ret = c.minSquareDistance(cr);
            if (c.owner().uuid().equals(id)) ret |= Long.MIN_VALUE;
            return ret;
        });
    }

    @Override
    protected int getContentSlot() {
        return 0;
    }

    @Override
    protected int getPreviousSlot() {
        return 1;
    }

    @Override
    protected @NotNull PlatformItem getPreviousExtra(@NotNull GuiInstance instance) {
        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.SPYGLASS),
                instance.runtime().lang("gui-sel-search")
        );
    }

    @Override
    protected int getNextSlot() {
        return 3;
    }

    @Override
    protected int getBackSlot() {
        return 2;
    }

    @Override
    protected @Nullable PlatformItem populateEntry(@NotNull GuiInstance instance, @NotNull Claim claim) {
        final PlatformUser owner = claim.owner();
        final ChunkReference curChunk = ChunkReference.of(instance.player().location());
        final String ownerName = owner.displayName();

        final List<String> lore = new ArrayList<>();
        lore.add(instance.runtime().lang("gui-sel-owned", ownerName));

        Set<ChunkReference> chunks = claim.chunks();
        int chunkCount = chunks.size();
        if (chunkCount == 1) {
            lore.add(instance.runtime().lang("gui-sel-chunk-count", chunkCount));
        } else {
            lore.add(instance.runtime().lang("gui-sel-chunk-count-plural", chunkCount));
        }
        if (chunkCount > 0) {
            ChunkReference c = chunks.iterator().next();
            lore.add(instance.runtime().lang("gui-sel-first-chunk", c.getCenterBlockX(), c.getCenterBlockZ()));
        }
        if (chunks.contains(curChunk)) {
            lore.add(instance.runtime().lang("gui-sel-within"));
        }

        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.GREEN_DYE),
                claim.name(),
                ColorTag.GREEN,
                lore
        );
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull Claim entry) {
        if (!entry.isValid()) {
            synchronized (this) {
                this.entries = null;
            }
            return GuiAction.repopulate();
        }
        return this.onClickClaim(instance, entry);
    }

    protected abstract @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim);

    @Override
    protected @NotNull GuiAction onClickExtra(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == this.getPreviousSlot()) {
            // Search
            return GuiAction.prompt(instance.runtime().lang("gui-sel-prompt"));
        }
        return GuiAction.nothing();
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        synchronized (this) {
            this.sort = Comparator.comparingInt((Claim c) -> StringUtil.distance(c.name(), response));
        }
        this.pagination.resetPage();
        return GuiAction.repopulate();
    }

}
