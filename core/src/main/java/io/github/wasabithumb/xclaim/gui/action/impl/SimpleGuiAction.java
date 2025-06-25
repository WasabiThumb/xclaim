package io.github.wasabithumb.xclaim.gui.action.impl;

import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.action.GuiActionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record SimpleGuiAction(
        @NotNull GuiActionType type
) implements GuiAction {

    public static final GuiAction NOTHING = new SimpleGuiAction(GuiActionType.NOTHING);
    public static final GuiAction REPOPULATE = new SimpleGuiAction(GuiActionType.REPOPULATE);
    public static final GuiAction EXIT = new SimpleGuiAction(GuiActionType.EXIT);

}
