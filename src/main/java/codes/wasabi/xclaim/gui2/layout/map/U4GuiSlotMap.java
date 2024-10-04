package codes.wasabi.xclaim.gui2.layout.map;

import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

@ApiStatus.Internal
class U4GuiSlotMap extends GuiSlotMap {

    private final byte[] data;
    public U4GuiSlotMap(@NotNull GuiLayout layout) {
        super(layout);

        final int size = (((this.w * this.h) - 1) >> 1) + 1;
        this.data = new byte[size];
        Arrays.fill(this.data, (byte) -1);

        this.populate(layout);
    }

    @Override
    public int max() {
        return 14;
    }

    @Override
    public @Range(from = -1L, to = 14L) int get(int x, int y) {
        if (oob(x, y)) return -1;
        int index = this.calculateIndex(x, y);
        int octet = Byte.toUnsignedInt(this.data[index >> 1]);
        if ((index & 1) == 1) {
            octet >>= 4;
        } else {
            octet &= 0xF;
        }
        return (octet == 0xF) ? -1 : octet;
    }

    @Override
    protected void set(int x, int y, int value) {
        int index = this.calculateIndex(x, y);
        int bIndex = index >> 1;

        byte data = this.data[bIndex];
        if ((index & 1) == 1) {
            data &= (byte) 0x0F;
            data |= (byte) (value << 4);
        } else {
            data &= (byte) 0xF0;
            data |= (byte) value;
        }
        this.data[bIndex] = data;
    }

}
