package codes.wasabi.xclaim.gui2.action.impl;

import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.action.GuiActionType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class PromptGuiAction implements GuiAction {

    private final Component text;
    public PromptGuiAction(@NotNull Component message) {
        this.text = message;
    }

    @Override
    public @NotNull GuiActionType type() {
        return GuiActionType.PROMPT;
    }

    public @NotNull Component message() {
        return this.text;
    }

}
