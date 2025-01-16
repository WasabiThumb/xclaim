package io.github.wasabithumb.xclaim.gui.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui.spec.impl.ClaimSelectorGuiSpec;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

public final class TransferableClaimSelectorGuiSpec extends ClaimSelectorGuiSpec {

    private transient Claim selection = null;

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        this.selection = claim;
        return GuiAction.prompt(instance.runtime().lang("gui-tx-prompt"));
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        final Claim selection = this.selection;
        if (selection == null) {
            // Shouldn't happen, just in case
            return GuiAction.exit();
        }

        PlatformUser user = instance.platform().users().matchUser(response);
        if (user == null) {
            instance.player().sendMessage(instance.runtime().lang("gui-tx-prompt-fail"));
            return GuiAction.exit();
        }

        return GuiAction.transfer(GuiSpecs.transferOwner(selection, user));
    }

    @Override
    protected boolean canDisplay(@NotNull Claim claim, @NotNull PlatformUser player) {
        return claim.owner().uuid().equals(player.uuid());
    }

}
