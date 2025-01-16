package io.github.wasabithumb.xclaim.gui.action.impl;

import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.action.GuiActionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class RepopulateGuiAction implements GuiAction {

    public static final RepopulateGuiAction INSTANCE = new RepopulateGuiAction();

    //

    @Override
    public @NotNull GuiActionType type() {
        return GuiActionType.REPOPULATE;
    }

}
