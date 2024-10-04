package codes.wasabi.xclaim.gui2.action;

import codes.wasabi.xclaim.gui2.action.impl.*;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import net.kyori.adventure.text.Component;
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
    static @NotNull GuiAction prompt(@NotNull Component message) {
        return new PromptGuiAction(message);
    }

    @Contract(pure = true)
    static @NotNull GuiAction exit() {
        return ExitGuiAction.INSTANCE;
    }

    //

    @NotNull GuiActionType type();

}
