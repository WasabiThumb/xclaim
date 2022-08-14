package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TransferPage extends Page {

    private static final ItemStack YES_ITEM = DisplayItem.create(
            Platform.get().getGreenConcreteMaterial(),
            XClaim.lang.getComponent("gui-tx-yes"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-tx-yes-line1"),
                    XClaim.lang.getComponent("gui-tx-yes-line2"),
                    XClaim.lang.getComponent("gui-tx-yes-line3")
            )
    );

    private static final ItemStack NO_ITEM = DisplayItem.create(
            Platform.get().getRedConcreteMaterial(),
            XClaim.lang.getComponent("gui-tx-no"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-tx-no-line1"),
                    XClaim.lang.getComponent("gui-tx-no-line2")
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
            head = Platform.get().preparePlayerSkull(
                    DisplayItem.create(
                            Platform.get().getPlayerHeadMaterial(),
                            Platform.get().playerDisplayName(matchPlayer)
                    )
            );
            ItemMeta im = head.getItemMeta();
            if (im != null) {
                if (im instanceof SkullMeta) Platform.get().setOwningPlayer((SkullMeta) im, matchPlayer);
            }
            head.setItemMeta(im);
        } else {
            head = Platform.get().preparePlayerSkull(
                    DisplayItem.create(
                            Platform.get().getPlayerHeadMaterial(),
                            XClaim.lang.get("unknown"),
                            NamedTextColor.RED
                    )
            );
        }
        setItem(4, head);
        setItem(11, YES_ITEM);
        setItem(15, NO_ITEM);
    }

    @Override
    public void onEnter() {
        clear();
        prompt(XClaim.lang.get("gui-tx-prompt"), (String s) -> {
            List<Player> matches = Bukkit.matchPlayer(s);
            if (matches.size() < 1) {
                Platform.getAdventure().player(getTarget()).sendMessage(XClaim.lang.getComponent("gui-tx-prompt-fail"));
                getParent().close();
                return;
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
                Platform.getAdventure().player(target).sendMessage(XClaim.lang.getComponent("gui-tx-success"));
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                switchPage(new MainPage(getParent()));
            }
        } else if (slot == 15) {
            switchPage(new MainPage(getParent()));
        }
    }

}
