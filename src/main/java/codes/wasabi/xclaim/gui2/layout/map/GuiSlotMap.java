package codes.wasabi.xclaim.gui2.layout.map;

import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@ApiStatus.NonExtendable
public abstract class GuiSlotMap {

    public static @NotNull GuiSlotMap create(@NotNull GuiLayout layout) {
        final int max = layout.getMaxSlot();
        if (max < 3) {
            return new U2GuiSlotMap(layout);
        } else if (max < 15) {
            return new U4GuiSlotMap(layout);
        } else {
            return new U8GuiSlotMap(layout);
        }
    }

    //

    protected final int w;
    protected final int h;
    protected GuiSlotMap(@NotNull GuiLayout layout) {
        if (layout.getMaxSlot() > this.max())
            throw new IllegalArgumentException("Layout has too many slots to fit in map (" + layout.getMaxSlot() + ")");

        this.w = layout.getWidth();
        this.h = layout.getHeight();
    }

    public abstract int max();

    public int width() {
        return this.w;
    }

    public int height() {
        return this.h;
    }

    public abstract @Range(from=-1L, to=255L) int get(int x, int y);

    protected abstract void set(int x, int y, int value);

    protected void set(int x, int y, int w, int h, int value) {
        for (int dy=0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                this.set(x + dx, y + dy, value);
            }
        }
    }

    //

    protected boolean oob(int x, int y) {
        return (x < 0 || y < 0 || x >= this.w || y >= this.h);
    }

    protected void populate(@NotNull GuiLayout layout) {
        int w, h;
        for (GuiSlot slot : layout) {
            w = slot.width();
            h = slot.height();
            if (w == 1 && h == 1) {
                this.set(slot.x(), slot.y(), slot.index());
            } else {
                this.set(slot.x(), slot.y(), w, h, slot.index());
            }
        }
    }

    protected int calculateIndex(int x, int y) {
        return (y * this.w) + x;
    }

}
