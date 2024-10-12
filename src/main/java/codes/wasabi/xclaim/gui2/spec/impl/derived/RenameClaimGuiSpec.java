package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import org.jetbrains.annotations.NotNull;

public final class RenameClaimGuiSpec extends ClaimSelectorGuiSpec {

    private Claim target = null;

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        synchronized (this) {
            this.target = claim;
        }
        return GuiAction.prompt(XClaim.lang.getComponent("gui-rename-chunk-prompt"));
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        Claim target;
        synchronized (this) {
            if (this.target == null) return super.onResponse(instance, response);
            target = this.target;
            this.target = null;
        }

        if (response.length() > 50) {
            Platform.getAdventure().player(instance.player())
                    .sendMessage(XClaim.lang.getComponent("gui-rename-chunk-fail"));
            return GuiAction.exit();
        }

        target.setName(response);
        return GuiAction.repopulate();
    }

}
