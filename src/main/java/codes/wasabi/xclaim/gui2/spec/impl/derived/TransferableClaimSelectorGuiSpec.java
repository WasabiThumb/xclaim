package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import codes.wasabi.xclaim.util.NameToPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class TransferableClaimSelectorGuiSpec extends ClaimSelectorGuiSpec {

    private transient Claim selection = null;

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        this.selection = claim;
        return GuiAction.prompt(XClaim.lang.getComponent("gui-tx-prompt"));
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        final Claim selection = this.selection;
        if (selection == null) {
            // Shouldn't happen, just in case
            return GuiAction.exit();
        }

        OfflinePlayer op = NameToPlayer.getPlayer(response);
        if (op == null) {
            instance.audience().sendMessage(XClaim.lang.getComponent("gui-tx-prompt-fail"));
            return GuiAction.exit();
        }

        return GuiAction.transfer(GuiSpecs.transferOwner(selection, op));
    }

    @Override
    protected boolean canDisplay(@NotNull Claim claim, @NotNull Player player) {
        return claim.getOwner().getUniqueId().equals(player.getUniqueId());
    }

}
