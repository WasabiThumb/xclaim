package codes.wasabi.xclaim.gui2.action.impl;

import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.action.GuiActionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class NothingGuiAction implements GuiAction {

    public static final NothingGuiAction INSTANCE = new NothingGuiAction();

    //

    @Override
    public @NotNull GuiActionType type() {
        return GuiActionType.NOTHING;
    }

}
