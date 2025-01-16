package io.github.wasabithumb.xclaim.gui.action.impl;

import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.action.GuiActionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class PromptGuiAction implements GuiAction {

    private final String text;
    public PromptGuiAction(@NotNull String message) {
        this.text = message;
    }

    @Override
    public @NotNull GuiActionType type() {
        return GuiActionType.PROMPT;
    }

    public @NotNull String message() {
        return this.text;
    }

}
