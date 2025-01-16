package io.github.wasabithumb.xclaim.gui2.layout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

public interface GuiLayout {

    default int getWidth() {
        return 9;
    }

    int getHeight();

    @NotNull List<GuiSlot> getSlots();

    @Nullable GuiSlot getSlot(@Range(from=0L, to=254L) int index);

    @Range(from=-1L, to=254L) int getMaxSlot();

    default void addSlot(@NotNull GuiSlot slot) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
