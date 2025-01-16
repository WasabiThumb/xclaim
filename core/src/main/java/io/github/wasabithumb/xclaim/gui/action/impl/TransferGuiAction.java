package io.github.wasabithumb.xclaim.gui.action.impl;

import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.action.GuiActionType;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
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
