package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.helper.PaginatedGuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ChunkReference;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ClaimSelectorGuiSpec extends PaginatedGuiSpec<Claim> {

    private static final ItemStack SEARCH_STACK = DisplayItem.create(
            Platform.get().getSpyglassMaterial(),
            XClaim.lang.getComponent("gui-sel-search")
    );

    //

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

        final Player player = instance.player();
        final Set<Claim> all = Claim.getAll();
        if (player.isOp()) return this.entries = new HashSet<>(all);

        final int sizeEstimate = Math.max(Math.floorDiv(all.size(), Bukkit.getOnlinePlayers().size() + 1), 8);
        final List<Claim> entries = new ArrayList<>(sizeEstimate);
        for (Claim c : all) {
            if (!this.canDisplay(c, player)) continue;
            entries.add(c);
        }

        return this.entries = entries;
    }

    protected boolean canDisplay(@NotNull Claim claim, @NotNull Player player)  {
        return claim.hasPermission(player, this.requiredPermission());
    }

    /** Not used if canDisplay is overriden */
    protected @NotNull Permission requiredPermission() {
        return Permission.MANAGE;
    }

    @Override
    protected synchronized final @NotNull Comparator<Claim> getSort(@NotNull GuiInstance instance) {
        if (this.sort != null) return this.sort;

        final Player player = instance.player();
        final UUID id = player.getUniqueId();
        final ChunkReference cr = ChunkReference.of(player.getLocation());

        // Default sort: Sort by distance (ascending), own claims first.
        return this.sort = Comparator.comparingLong((Claim c) -> {
            long ret = c.minSquareDistance(cr);
            if (c.getOwner().getUniqueId().equals(id)) ret |= Long.MIN_VALUE;
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
    protected @NotNull ItemStack getPreviousExtra() {
        return SEARCH_STACK;
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
    protected @Nullable ItemStack populateEntry(@NotNull GuiInstance instance, @NotNull Claim claim) {
        final XCPlayer owner = claim.getOwner();
        final ChunkReference curChunk = ChunkReference.of(instance.player().getLocation());
        final Player ownerOnline = owner.getPlayer();
        Component ownerName;
        if (ownerOnline != null) {
            ownerName = Platform.get().playerDisplayName(ownerOnline);
        } else {
            String nm = owner.getName();
            if (nm == null) nm = owner.getUniqueId().toString();
            ownerName = Component.text(nm);
        }

        final List<Component> lore = new ArrayList<>();
        lore.add(XClaim.lang.getComponent("gui-sel-owned", ownerName));

        Set<ChunkReference> chunks = claim.getChunks();
        int chunkCount = chunks.size();
        if (chunkCount == 1) {
            lore.add(XClaim.lang.getComponent("gui-sel-chunk-count", chunkCount));
        } else {
            lore.add(XClaim.lang.getComponent("gui-sel-chunk-count-plural", chunkCount));
        }
        if (chunkCount > 0) {
            ChunkReference c = chunks.iterator().next();
            lore.add(XClaim.lang.getComponent("gui-sel-first-chunk", c.getCenterBlockX(), c.getCenterBlockZ()));
        }
        if (chunks.contains(curChunk)) {
            lore.add(XClaim.lang.getComponent("gui-sel-within"));
        }

        return DisplayItem.create(
                Platform.get().getGreenToken(),
                Component.text(claim.getName()).color(NamedTextColor.GREEN),
                lore
        );
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull Claim entry) {
        if (!entry.isCanonical()) {
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
            return GuiAction.prompt(XClaim.lang.getComponent("gui-sel-prompt"));
        }
        return GuiAction.nothing();
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        final LevenshteinDistance strDist = LevenshteinDistance.getDefaultInstance();
        synchronized (this) {
            this.sort = Comparator.comparingInt((Claim c) -> strDist.apply(c.getName(), response));
        }
        this.pagination.resetPage();
        return GuiAction.repopulate();
    }

}
