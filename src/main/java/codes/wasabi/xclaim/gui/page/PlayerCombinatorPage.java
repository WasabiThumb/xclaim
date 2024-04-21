package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.NameToPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public abstract class PlayerCombinatorPage extends Page {

    public static @NotNull PlayerCombinatorPage withList(@NotNull GUIHandler parent, @NotNull List<OfflinePlayer> list) {
        return withList(parent, list, (OfflinePlayer p) -> {});
    }

    public static @NotNull PlayerCombinatorPage withList(@NotNull GUIHandler parent, @NotNull List<OfflinePlayer> list, @NotNull Consumer<OfflinePlayer> cb) {
        return new PlayerCombinatorPage(parent) {
            @Override
            protected @NotNull List<OfflinePlayer> getList() {
                return list;
            }

            @Override
            protected void add(@NotNull OfflinePlayer ply) {
                list.add(ply);
            }

            @Override
            protected void remove(@NotNull OfflinePlayer ply) {
                list.remove(ply);
            }

            @Override
            protected void onSelect(@NotNull OfflinePlayer ply) {
                cb.accept(ply);
            }
        };
    }

    public PlayerCombinatorPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    private int pageIndex = 0;
    private final Map<Integer, OfflinePlayer> assoc = new HashMap<>();
    private void populate() {
        clear();
        List<OfflinePlayer> players = getList();
        int maxIndex = Math.max(players.size() - 1, 0) / 18;
        pageIndex = Math.min(Math.max(pageIndex, 0), maxIndex);
        int from = pageIndex * 18;
        assoc.clear();
        for (int i=0; i < 18; i++) {
            int listIndex = i + from;
            if (listIndex >= players.size()) break;
            OfflinePlayer ply = players.get(listIndex);
            assoc.put(i, ply);
            ItemStack is = Platform.get().preparePlayerSkull(new ItemStack(Platform.get().getPlayerHeadMaterial(), 1));
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                String realName = ply.getName();
                if (realName == null) realName = ply.getUniqueId().toString();
                Component niceName;
                if (ply instanceof Player) {
                    niceName = Platform.get().playerDisplayName((Player) ply);
                } else {
                    niceName = Component.text(realName);
                }
                meta.addItemFlags(ItemFlag.values());
                Platform.get().metaDisplayName(meta, niceName);
                Platform.get().metaLore(meta, Collections.singletonList(Component.text(realName).color(NamedTextColor.GRAY)));
                if (meta instanceof SkullMeta) Platform.get().setOwningPlayer((SkullMeta) meta, ply);
            }
            is.setItemMeta(meta);
            setItem(i, is);
        }
        if (pageIndex > 0) {
            setItem(18, DisplayItem.create(Material.ARROW, XClaim.lang.getComponent("gui-comb-previous")));
        }
        if (pageIndex < maxIndex) {
            setItem(26, DisplayItem.create(Material.ARROW, XClaim.lang.getComponent("gui-comb-next")));
        }
        setItem(21, DisplayItem.create(Material.EMERALD, XClaim.lang.getComponent("gui-comb-add")));
        setItem(23, DisplayItem.create(Material.BARRIER, XClaim.lang.getComponent("gui-comb-back")));
    }

    @Override
    public void onEnter() {
        populate();
    }

    @Override
    public void onExit() {

    }

    @Override
    public void onClick(int slot) {
        if (slot == 18) {
            pageIndex--;
            populate();
        } else if (slot == 26) {
            pageIndex++;
            populate();
        } else if (slot == 23) {
            goBack();
        } else if (slot == 21) {
            prompt("gui-comb-prompt", (String name) -> {
                OfflinePlayer ply = NameToPlayer.getPlayer(name);
                if (ply == null) {
                    Platform.getAdventure().player(getTarget()).sendMessage(XClaim.lang.getComponent("gui-comb-prompt-fail"));
                    return;
                }
                add(ply);
                Platform.getAdventure().player(getTarget()).sendMessage(XClaim.lang.getComponent("gui-comb-prompt-success"));
                populate();
            });
        } else if (slot < 18) {
            OfflinePlayer ply = assoc.get(slot);
            if (ply != null) {
                onSelect(ply);
            }
            if (getParent().isOpen() && getParent().getActivePage() == this) populate();
        }
    }

    protected abstract @NotNull List<OfflinePlayer> getList();

    protected abstract void add(@NotNull OfflinePlayer ply);

    protected abstract void remove(@NotNull OfflinePlayer ply);

    protected abstract void onSelect(@NotNull OfflinePlayer ply);

    protected void goBack() {
        switchPage(new MainPage(getParent()));
    }

}
