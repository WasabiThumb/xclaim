package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import org.jetbrains.annotations.NotNull;

public final class RenameClaimGuiSpec extends ClaimSelectorGuiSpec {

    private Claim target = null;

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        synchronized (this) {
            this.target = claim;
        }
        return GuiAction.prompt(instance.runtime().lang("gui-rename-chunk-prompt"));
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        Claim target;
        synchronized (this) {
            if (this.target == null) return super.onResponse(instance, response);
            target = this.target;
            this.target = null;
        }

        boolean success = target.rename(instance.player())
                .setNewName(response)
                .commit()
                .isSuccess();

        return success ? GuiAction.repopulate() : GuiAction.exit();
    }

}
