package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimTransferOwnerEvent;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class TransferOwnerGuiSpec implements GuiSpec {

    private static final ItemStack YES_STACK = DisplayItem.create(
            Platform.get().getGreenConcreteMaterial(),
            XClaim.lang.getComponent("gui-tx-yes"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-tx-yes-line1"),
                    XClaim.lang.getComponent("gui-tx-yes-line2"),
                    XClaim.lang.getComponent("gui-tx-yes-line3")
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Platform.get().getRedConcreteMaterial(),
            XClaim.lang.getComponent("gui-tx-no"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-tx-no-line1"),
                    XClaim.lang.getComponent("gui-tx-no-line2")
            )
    );

    private final Claim claim;
    private final OfflinePlayer target;
    public TransferOwnerGuiSpec(@NotNull Claim claim, @NotNull OfflinePlayer target) {
        this.claim = claim;
        this.target = target;
    }

    @Override
    public @NotNull String layout() {
        return "transfer-owner";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, YES_STACK);
        instance.set(1, NO_STACK);
        instance.set(2, this.getTargetHead());
    }

    private @NotNull ItemStack getTargetHead() {
        Component targetName;
        if (this.target instanceof Player) {
            targetName = Platform.get().playerDisplayName((Player) this.target);
        } else {
            String targetNameStr = this.target.getName();
            if (targetNameStr == null) targetNameStr = XClaim.lang.get("unknown") + " (" + this.target.getUniqueId() + ")";
            targetName = Component.text(targetNameStr).color(NamedTextColor.GRAY);
        }

        final ItemStack head = Platform.get().preparePlayerSkull(
                DisplayItem.create(
                        Platform.get().getPlayerHeadMaterial(),
                        targetName
                )
        );

        ItemMeta im = head.getItemMeta();
        if (im != null) {
            if (im instanceof SkullMeta) Platform.get().setOwningPlayer((SkullMeta) im, this.target);
        }
        head.setItemMeta(im);
        return head;
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 1) {
            return GuiAction.transfer(GuiSpecs.transferableClaimSelector());
        } else if (slot.index() != 0) {
            return GuiAction.nothing();
        }

        if (!XClaimEvent.dispatch(new XClaimTransferOwnerEvent(
                instance.player(),
                this.claim,
                this.claim.getOwner(),
                XCPlayer.of(this.target)
        ))) {
            return GuiAction.exit();
        }

        this.claim.setOwner(this.target);
        this.claim.setUserPermission(instance.player(), Permission.MANAGE, true);

        instance.audience().sendMessage(XClaim.lang.getComponent("gui-tx-success"));
        instance.playSound(Platform.get().getLevelSound(), 1f, 1f);
        return GuiAction.transfer(GuiSpecs.transferableClaimSelector());
    }

}
