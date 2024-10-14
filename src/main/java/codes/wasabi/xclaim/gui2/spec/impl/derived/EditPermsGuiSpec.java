package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import org.jetbrains.annotations.NotNull;

public final class EditPermsGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        return GuiAction.transfer(GuiSpecs.permissionOverview(claim));
    }

}
