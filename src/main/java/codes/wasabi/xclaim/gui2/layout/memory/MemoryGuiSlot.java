package codes.wasabi.xclaim.gui2.layout.memory;

import codes.wasabi.xclaim.gui2.layout.GuiBasis;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import org.jetbrains.annotations.NotNull;

public class MemoryGuiSlot implements GuiSlot {

    private final int index;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final GuiBasis basis;
    public MemoryGuiSlot(int index, int x, int y, int width, int height, @NotNull GuiBasis basis) {
        if (index < 0 || index > 255) throw new IllegalArgumentException("Slot index must be in range 0 to 255");
        if (x < 0) throw new IllegalArgumentException("X coordinate may not be negative");
        if (y < 0) throw new IllegalArgumentException("Y coordinate may not be negative");
        if (width < 1) throw new IllegalArgumentException("Width must be at least 1");
        if (height < 1) throw new IllegalArgumentException("Height must be at least 1");

        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.basis = basis;
    }

    public MemoryGuiSlot(int index, int x, int y, int width, int height) {
        this(index, x, y, width, height, GuiBasis.LEFT);
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public @NotNull GuiBasis basis() {
        return this.basis;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof MemoryGuiSlot) {
            MemoryGuiSlot other = (MemoryGuiSlot) obj;
            if (this.index == other.index &&
                    this.x == other.x &&
                    this.y == other.y &&
                    this.width == other.width &&
                    this.height == other.height
            ) return true;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int ret = 7;
        ret = 31 * ret + this.index;
        ret = 31 * ret + this.x;
        ret = 31 * ret + this.y;
        ret = 31 * ret + this.width;
        ret = 31 * ret + this.height;
        return ret;
    }

    @Override
    public String toString() {
        return "GuiSlot[" + this.basis.getChar() + "#" + this.index + " (" + this.x + ", " + this.y + ") ("
                + this.width + ", " + this.height + ")]";
    }

}
