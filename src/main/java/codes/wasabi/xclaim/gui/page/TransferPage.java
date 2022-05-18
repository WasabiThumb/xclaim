package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TransferPage extends Page {

    private static final ItemStack YES_ITEM = DisplayItem.create(
            Material.GREEN_CONCRETE,
            Component.text("Yes").color(NamedTextColor.DARK_GREEN),
            Arrays.asList(
                    Component.text("I am sure that I want").color(NamedTextColor.GRAY),
                    Component.text("to transfer ownership to").color(NamedTextColor.GRAY),
                    Component.text("this user").color(NamedTextColor.GRAY)
            )
    );

    private static final ItemStack NO_ITEM = DisplayItem.create(
            Material.RED_CONCRETE,
            Component.text("No").color(NamedTextColor.DARK_RED),
            Arrays.asList(
                    Component.text("Take me back to").color(NamedTextColor.GRAY),
                    Component.text("safety!").color(NamedTextColor.GRAY)
            )
    );

    private final Claim claim;
    public TransferPage(@NotNull GUIHandler parent, @NotNull Claim claim) {
        super(parent);
        this.claim = claim;
    }

    private Player matchPlayer = null;
    private void populate() {
        ItemStack head;
        if (matchPlayer != null) {
            head = DisplayItem.create(Material.PLAYER_HEAD, matchPlayer.displayName());
            head.editMeta(SkullMeta.class, (SkullMeta sm) -> sm.setOwningPlayer(matchPlayer));
        } else {
            head = DisplayItem.create(Material.PLAYER_HEAD, "Unknown Player", NamedTextColor.RED);
        }
        setItem(4, head);
        setItem(11, YES_ITEM);
        setItem(15, NO_ITEM);
    }

    @Override
    public void onEnter() {
        clear();
        prompt("Enter the username of the player to transfer to: ", (String s) -> {
            List<Player> matches = Bukkit.matchPlayer(s);
            if (matches.size() < 1) {
                getTarget().sendMessage(Component.text("* Cannot find that player!").color(NamedTextColor.RED));
                getParent().close();
            }
            matchPlayer = matches.get(0);
            populate();
        });
    }

    @Override
    public void onClick(int slot) {
        if (slot == 11) {
            if (matchPlayer != null) {
                Player target = getTarget();
                claim.setOwner(matchPlayer);
                claim.setUserPermission(target, Permission.MANAGE, true);
                target.sendMessage(Component.text("* Ownership transferred").color(NamedTextColor.GREEN));
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                switchPage(new MainPage(getParent()));
            }
        } else if (slot == 15) {
            switchPage(new MainPage(getParent()));
        }
    }

}
