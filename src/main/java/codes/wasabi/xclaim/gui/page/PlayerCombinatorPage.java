package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.NameToPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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
            ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);
            is.editMeta((ItemMeta meta) -> {
                String realName = ply.getName();
                if (realName == null) realName = ply.getUniqueId().toString();
                Component niceName;
                if (ply instanceof Player onlinePlayer) {
                    niceName = onlinePlayer.displayName();
                } else {
                    niceName = Component.text(realName);
                }
                meta.addItemFlags(ItemFlag.values());
                meta.displayName(niceName);
                if (!Objects.equals(realName, PlainTextComponentSerializer.plainText().serializeOrNull(niceName))) {
                    meta.lore(Collections.singletonList(Component.text(realName).color(NamedTextColor.GRAY)));
                }
            });
            is.editMeta((ItemMeta im) -> {
                if (im instanceof SkullMeta sm) sm.setOwningPlayer(ply);
            });
            setItem(i, is);
        }
        if (pageIndex > 0) {
            setItem(18, DisplayItem.create(Material.ARROW, "Previous Page", NamedTextColor.GOLD));
        }
        if (pageIndex < maxIndex) {
            setItem(26, DisplayItem.create(Material.ARROW, "Next Page", NamedTextColor.GOLD));
        }
        setItem(21, DisplayItem.create(Material.EMERALD, "Add Player", NamedTextColor.GREEN));
        setItem(23, DisplayItem.create(Material.BARRIER, "Back", NamedTextColor.RED));
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
            prompt("Enter player name: ", (String name) -> {
                OfflinePlayer ply = NameToPlayer.getPlayer(name);
                if (ply == null) {
                    getTarget().sendMessage(Component.text("* Couldn't find a player with that name.").color(NamedTextColor.RED));
                    return;
                }
                add(ply);
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
