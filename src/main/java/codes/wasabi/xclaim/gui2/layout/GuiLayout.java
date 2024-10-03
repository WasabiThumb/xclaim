package codes.wasabi.xclaim.gui2.layout;

import org.apache.commons.lang3.stream.IntStreams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public interface GuiLayout extends Iterable<GuiSlot> {

    default int getWidth() {
        return 9;
    }

    int getHeight();

    @NotNull List<GuiSlot> getSlots();

    @Nullable GuiSlot getSlot(int index);

    int getMaxSlot();

    default void addSlot(@NotNull GuiSlot slot) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    default @NotNull Iterator<GuiSlot> iterator() {
        return IntStreams.rangeClosed(this.getMaxSlot())
                .mapToObj(this::getSlot)
                .filter(Objects::nonNull)
                .iterator();
    }

}
