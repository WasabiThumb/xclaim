package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClaimSelectorPage extends Page {

    private final Consumer<Claim> cb;
    private int pageIndex = 0;
    public ClaimSelectorPage(@NotNull GUIHandler parent, @NotNull Consumer<Claim> callback) {
        super(parent);
        cb = callback;
    }

    protected boolean showClaim(@NotNull Claim claim, @NotNull OfflinePlayer ply) {
        return claim.hasPermission(ply, Permission.MANAGE);
    }

    private Comparator<Claim> sorter = Comparator.comparing(Claim::getName, Comparator.naturalOrder());
    private final Map<Integer, Claim> slotAssoc = new HashMap<>();
    private void populate() {
        clear();
        slotAssoc.clear();
        Player target = getTarget();
        List<Claim> claims = Claim.getByOwner(getTarget()).stream().sorted(sorter).collect(Collectors.toCollection(ArrayList::new));
        Claim.getAll().stream().filter((Claim c) -> (!claims.contains(c)) && showClaim(c, target)).sorted(sorter).forEach(claims::add);
        Claim within = Claim.getByChunk(target.getLocation().getChunk());
        boolean addedWithin = false;
        if (within != null) {
            if (showClaim(within, target)) {
                addedWithin = true;
                claims.remove(within);
                claims.add(0, within);
            }
        }
        int maxPage = Math.max((claims.size() - 1) / 24, 0);
        pageIndex = Math.min(Math.max(pageIndex, 0), maxPage);
        int i = pageIndex * 24;
        outer:
        for (int y = 0; y < 3; y++) {
            for (int x=0; x < 8; x++) {
                if (i >= claims.size()) break outer;
                int idx = getPageIndex(x, y);
                Claim claim = claims.get(i);
                XCPlayer owner = claim.getOwner();
                Component name;
                Player ply;
                if ((ply = owner.getPlayer()) != null) {
                    name = Platform.get().playerDisplayName(ply);
                } else {
                    String nm = owner.getName();
                    if (nm == null) nm = owner.getUniqueId().toString();
                    name = Component.text(nm);
                }
                slotAssoc.put(idx, claim);
                List<Component> lore = new ArrayList<>();
                lore.add(XClaim.lang.getComponent("gui-sel-owned", name));
                Set<Chunk> chunks = claim.getChunks();
                int chunkCount = chunks.size();
                if (chunkCount == 1) {
                    lore.add(XClaim.lang.getComponent("gui-sel-chunk-count", chunkCount));
                } else {
                    lore.add(XClaim.lang.getComponent("gui-sel-chunk-count-plural", chunkCount));
                }
                if (chunkCount > 0) {
                    Chunk c = chunks.iterator().next();
                    Block b = c.getBlock(8, Platform.get().getWorldMinHeight(c.getWorld()), 8);
                    lore.add(XClaim.lang.getComponent("gui-sel-first-chunk", b.getX(), b.getZ()));
                }
                if (i == 0 && addedWithin) {
                    lore.add(XClaim.lang.getComponent("gui-sel-within"));
                }
                setItem(idx, DisplayItem.create(
                        Material.GREEN_DYE,
                        Component.text(claim.getName()).color(NamedTextColor.GREEN),
                        lore
                ));
                i++;
            }
        }
        if (pageIndex > 0) {
            setItem(8, DisplayItem.create(Material.ARROW, XClaim.lang.getComponent("gui-sel-previous")));
        } else {
            setItem(8, DisplayItem.create(Platform.get().getSpyglassMaterial(), XClaim.lang.getComponent("gui-sel-search")));
        }
        //
        setItem(17, DisplayItem.create(Material.BARRIER, XClaim.lang.getComponent("gui-sel-cancel")));
        if (pageIndex < maxPage) {
            setItem(26, DisplayItem.create(Material.ARROW, XClaim.lang.getComponent("gui-sel-next")));
        }
    }

    @Override
    public void onEnter() {
        populate();
    }

    @Override
    public void onClick(int slot) {
        if (slot == 17) {
            switchPage(new MainPage(getParent()));
        } else if (slot == 8) {
            if (pageIndex > 0) {
                pageIndex--;
                populate();
            } else {
                prompt(XClaim.lang.get("gui-sel-prompt"), (String term) -> {
                    sorter = Comparator.comparingInt((Claim claim) -> LevenshteinDistance.getDefaultInstance().apply(claim.getName(), term));
                    populate();
                });
            }
        } else if (slot == 26) {
            pageIndex++;
            populate();
        } else {
            Claim claim = slotAssoc.get(slot);
            if (claim != null) {
                cb.accept(claim);
                if (Objects.equals(getParent().getActivePage(), this) && getParent().isOpen()) populate();
            }
        }
    }

}
