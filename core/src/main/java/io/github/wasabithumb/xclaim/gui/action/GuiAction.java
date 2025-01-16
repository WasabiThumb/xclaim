package io.github.wasabithumb.xclaim.gui.action;

import io.github.wasabithumb.xclaim.gui.action.impl.*;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface GuiAction {

    @Contract(pure = true)
    static @NotNull GuiAction nothing() {
        return NothingGuiAction.INSTANCE;
    }

    @Contract(pure = true)
    static @NotNull GuiAction repopulate() {
        return RepopulateGuiAction.INSTANCE;
    }

    @Contract("_ -> new")
    static @NotNull GuiAction transfer(@NotNull GuiSpec target) {
        return new TransferGuiAction(target);
    }

    @Contract("_ -> new")
    static @NotNull GuiAction prompt(@NotNull String message) {
        return new PromptGuiAction(message);
    }

    @Contract(pure = true)
    static @NotNull GuiAction exit() {
        return ExitGuiAction.INSTANCE;
    }

    //

    @NotNull GuiActionType type();

}
