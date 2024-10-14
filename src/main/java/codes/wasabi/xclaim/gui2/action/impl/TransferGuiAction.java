package codes.wasabi.xclaim.gui2.action.impl;

import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.action.GuiActionType;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class TransferGuiAction implements GuiAction {

    private final GuiSpec target;
    public TransferGuiAction(@NotNull GuiSpec target) {
        this.target = target;
    }

    @Override
    public @NotNull GuiActionType type() {
        return GuiActionType.TRANSFER;
    }

    public @NotNull GuiSpec target() {
        return this.target;
    }

}
