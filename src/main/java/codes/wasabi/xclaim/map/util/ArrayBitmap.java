package codes.wasabi.xclaim.map.util;

import org.jetbrains.annotations.Nullable;

public class ArrayBitmap implements Bitmap {

    private static final byte FULL_BYTE = (byte) 0xFF;

    private final byte[] data;
    private final int width;
    private final int height;

    protected ArrayBitmap(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public ArrayBitmap(int width, int height) throws IllegalArgumentException {
        if (width < 0) throw new IllegalArgumentException("Width cannot be negative");
        if (height < 0) throw new IllegalArgumentException("Height cannot be negative");

        final int bytes = (int) ((((((long) width) * ((long) height)) - 1L) >> 3L) + 1L);

        this.data = new byte[bytes];
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean getPixel(int x, int y) {
        if (x < 0 || x >= this.width) return false;
        if (y < 0 || y >= this.height) return false;

        final int index = (y * this.width) + x;
        final int byteIndex = index >> 3;
        final byte flag = (byte) (1 << (index & 7));

        return (this.data[byteIndex] & flag) == flag;
    }

    public void setPixel(int x, int y) {
        this.setPixel(x, y, true);
    }

    public void setPixel(int x, int y, boolean value) {
        if (x < 0 || x >= this.width)
            throw new IndexOutOfBoundsException("X " + x + " out of bounds for width " + this.width);
        if (y < 0 || y >= this.height)
            throw new IndexOutOfBoundsException("Y " + x + " out of bounds for height " + this.height);

        final int index = (y * this.width) + x;
        final int byteIndex = index >> 3;
        final int flag = 1 << (index & 7);

        if (value) {
            this.data[byteIndex] |= ((byte) flag);
        } else {
            this.data[byteIndex] &= ((byte) (~flag));
        }
    }

    public @Nullable Point firstUnset() {
        byte b = 0;
        int bIndex = -1;

        for (int i=0; i < this.data.length; i++) {
            b = this.data[i];
            if (b == FULL_BYTE) continue;
            bIndex = i;
            break;
        }

        if (bIndex == -1) return null;
        bIndex <<= 3;

        final int ib = b & 0xFF;
        if (ib != 0) {
            final int startBit = Integer.numberOfTrailingZeros(~ib);
            final int endBit = 32 - Integer.numberOfLeadingZeros(ib);

            int flag;
            for (int z = startBit; z <= endBit; z++) {
                flag = (1 << z);
                if ((ib & flag) == 0) {
                    bIndex |= z;
                    break;
                }
            }
        }

        final int y = Math.floorDiv(bIndex, this.width);
        final int x = bIndex - (y * this.width);

        return new Point(x, y);
    }

}
