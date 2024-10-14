package codes.wasabi.xclaim.gui2.spec;

import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import org.jetbrains.annotations.NotNull;

public interface GuiSpec {

    @NotNull String layout();

    void populate(@NotNull GuiInstance instance);

    @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index);

    default @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        return GuiAction.nothing();
    }

}
