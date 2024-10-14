package codes.wasabi.xclaim.gui2.layout.map;

import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

@ApiStatus.Internal
class U8GuiSlotMap extends GuiSlotMap {

    private final byte[] data;
    public U8GuiSlotMap(@NotNull GuiLayout layout) {
        super(layout);

        final int size = (this.w * this.h);
        this.data = new byte[size];
        Arrays.fill(this.data, (byte) -1);

        this.populate(layout);
    }

    @Override
    public int max() {
        return 254;
    }

    @Override
    public @Range(from = -1L, to = 254L) int get(int x, int y) {
        if (oob(x, y)) return -1;
        int index = this.calculateIndex(x, y);
        byte b = this.data[index];
        if (b == -1) return -1;
        return b & 0xFF;
    }

    @Override
    protected void set(int x, int y, int value) {
        int index = this.calculateIndex(x, y);
        this.data[index] = (byte) value;
    }

}
