package codes.wasabi.xclaim.gui2.layout;

import org.jetbrains.annotations.NotNull;

public interface GuiSlot {

    int index();

    default byte compactIndex() {
        return (byte) (this.index() & 0xFF);
    }

    int x();

    int y();

    int width();

    int height();

    default @NotNull GuiBasis basis() {
        return GuiBasis.LEFT;
    }

    default int capacity() {
        return this.width() * this.height();
    }

    default int calculatePages(int count) {
        if (count == 0) return 1;
        return Math.floorDiv(count - 1, this.capacity()) + 1;
    }

    default int calculateLocalIndex(int x, int y) {
        return ((y - this.y()) * this.width()) + x - this.x();
    }

}
