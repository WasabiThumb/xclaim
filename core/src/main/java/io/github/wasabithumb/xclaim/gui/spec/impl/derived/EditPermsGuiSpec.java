package io.github.wasabithumb.xclaim.gui.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui.spec.impl.ClaimSelectorGuiSpec;
import org.jetbrains.annotations.NotNull;

public final class EditPermsGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        return GuiAction.transfer(GuiSpecs.permissionOverview(claim));
    }

}
