package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.api.Claim;
import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.api.event.XClaimDeleteClaimEvent;
import io.github.wasabithumb.xclaim.api.event.XClaimEvent;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
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
