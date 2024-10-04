package codes.wasabi.xclaim.gui2.layout.map;

import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

@ApiStatus.Internal
class U2GuiSlotMap extends GuiSlotMap {

    private final byte[] data;
    public U2GuiSlotMap(@NotNull GuiLayout layout) {
        super(layout);

        final int size = (((this.w * this.h) - 1) >> 2) + 1;
        this.data = new byte[size];
        Arrays.fill(this.data, (byte) -1);

        this.populate(layout);
    }

    @Override
    public int max() {
        return 2;
    }

    @Override
    public @Range(from = -1L, to = 2L) int get(int x, int y) {
        if (oob(x, y)) return -1;
        int index = this.calculateIndex(x, y);
        int octet = Byte.toUnsignedInt(this.data[index >> 2]);
        switch (index & 3) {
            case 0:
                octet &= 0b11;
                break;
            case 1:
                octet = (octet >> 2) & 0b11;
                break;
            case 2:
                octet = (octet >> 4) & 0b11;
                break;
            case 3:
                octet >>= 6;
                break;
        }
        return (octet == 3) ? -1 : octet;
    }

    @Override
    protected void set(int x, int y, int value) {
        int index = this.calculateIndex(x, y);
        int bIndex = index >> 2;
        int shl = ((index & 3) << 1);

        byte data = this.data[bIndex];
        data &= (byte) (~(0b11 << shl));
        data |= (byte) (value << shl);
        this.data[bIndex] = data;
    }

}
