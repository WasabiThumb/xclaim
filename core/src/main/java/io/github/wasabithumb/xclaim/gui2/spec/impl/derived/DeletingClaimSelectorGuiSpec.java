package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import org.jetbrains.annotations.NotNull;

public final class DeletingClaimSelectorGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        // TODO: Handle fail state better
        boolean success = claim.modifyChunks(instance.player())
                .clear()
                .commit()
                .isSuccess();

        if (!success) return GuiAction.nothing();

        this.entries.remove(claim);
        this.markForceUpdate();
        return GuiAction.repopulate();
    }

    @Override
    protected @NotNull Permission requiredPermission() {
        return Permission.DELETE;
    }

}
