package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
        claims.addAll(Claim.getAll().stream().filter((Claim c) -> (!claims.contains(c)) && showClaim(c, target)).sorted(sorter).toList());
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
                OfflinePlayer owner = claim.getOwner();
                Component name;
                Player ply;
                if ((ply = owner.getPlayer()) != null) {
                    name = ply.displayName();
                } else {
                    String nm = owner.getName();
                    if (nm == null) nm = owner.getUniqueId().toString();
                    name = Component.text(nm);
                }
                slotAssoc.put(idx, claim);
                List<Component> lore = new ArrayList<>();
                lore.add(Component.empty()
                        .append(Component.text("Owned by ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                        .append(name.color(NamedTextColor.GOLD))
                );
                int chunkCount = claim.getChunks().size();
                lore.add(Component.empty()
                        .append(Component.text(chunkCount).color(NamedTextColor.GOLD))
                        .append(Component.text(" chunk" + (chunkCount == 1 ? "" : "s")).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                );
                if (i == 0 && addedWithin) {
                    lore.add(Component.empty()
                            .append(Component.text("Currently within").color(NamedTextColor.GRAY))
                    );
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
            setItem(8, DisplayItem.create(Material.ARROW, "Previous", NamedTextColor.GOLD));
        } else {
            setItem(8, DisplayItem.create(Material.SPYGLASS, "Search", NamedTextColor.DARK_PURPLE));
        }
        //
        setItem(17, DisplayItem.create(Material.BARRIER, "Cancel", NamedTextColor.RED));
        if (pageIndex < maxPage) {
            setItem(26, DisplayItem.create(Material.ARROW, "Next", NamedTextColor.GOLD));
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
                prompt("Enter search term: ", (String term) -> {
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
