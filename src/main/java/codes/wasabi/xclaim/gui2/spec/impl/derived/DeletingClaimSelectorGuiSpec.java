package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.event.XClaimDeleteClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import org.jetbrains.annotations.NotNull;

public final class DeletingClaimSelectorGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        if (!XClaimEvent.dispatch(new XClaimDeleteClaimEvent(instance.player(), claim))) {
            return GuiAction.exit();
        }
        claim.unclaim();
        this.entries.remove(claim);
        this.markForceUpdate();
        return GuiAction.repopulate();
    }

    @Override
    protected @NotNull Permission requiredPermission() {
        return Permission.DELETE;
    }

}
