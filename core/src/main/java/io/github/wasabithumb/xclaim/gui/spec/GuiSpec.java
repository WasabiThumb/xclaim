package io.github.wasabithumb.xclaim.gui.spec;

import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import org.jetbrains.annotations.NotNull;

public interface GuiSpec {

    @NotNull String layout();

    void populate(@NotNull GuiInstance instance);

    @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index);

    default @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        return GuiAction.nothing();
    }

    /**
     * If true, the GUI manager will execute {@link #onResponse(GuiInstance, String) onResponse} asynchronously.
     */
    default boolean asyncResponse() {
        return false;
    }

}
