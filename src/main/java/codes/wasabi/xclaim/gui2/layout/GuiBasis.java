package codes.wasabi.xclaim.gui2.layout;

import org.jetbrains.annotations.Range;

public enum GuiBasis {
    LEFT,
    CENTER,
    EVEN,
    RIGHT;

    private static final byte[][] EVEN_TABLE = new byte[][] {
            new byte[] { 66, 97, 71, 19, 87, 2, 70, -127, 35, 86, 113, 35, 69, 103, 1, 35, 86, 120 },
            new byte[] { 50, 81, 53, 2, 70, 2, 53, 112, 35, 69, 112, 18, 52, 87 },
            new byte[] { 50, 65, 53, 2, 70, 2, 52, 96, 18, 69, 96 },
            new byte[] { 33, 64, 36, 18, 52, 1, 35, 64 },
            new byte[] { 33, 49, 35, 1, 52 },
            new byte[] { 17, 32, 18 },
            new byte[] { 16, 32 }
    };

    private static int organizeEven(int index, int innerSize, @Range(from = 3L, to = 9L) int outerSize) {
        if (innerSize > 1) {
            index += Math.floorDiv(innerSize * (innerSize - 1), 2);
        }
        final int value = Byte.toUnsignedInt(EVEN_TABLE[9 - outerSize][index >> 1]);
        return (index & 1) == 1 ? (value & 0xF) : (value >> 4);
    }

    //

    /**
     * Remaps an index (0 <= index < innerSize) given the number of indices inside the container (innerSize), and the
     * width of the container (outerSize), via the pattern indicated by this basis.
     */
    public int organize(int index, int innerSize, int outerSize) {
        if (index < 0) throw new IndexOutOfBoundsException("Index cannot be negative");
        if (index >= innerSize) throw new IndexOutOfBoundsException("Index cannot meet or exceed inner size");
        if (innerSize >= outerSize) return Math.min(index, outerSize - 1);
        switch (this) {
            case EVEN:
                if (outerSize == 2) return 0;
                if (outerSize > 9) throw new IllegalArgumentException("Outer size cannot be more than 9");
                return organizeEven(index, innerSize, outerSize);
            case RIGHT:
                return outerSize - 1 - index;
            case CENTER:
                return Math.floorDiv(outerSize - innerSize, 2) + index;
        }
        return index;
    }

    /**
     * Reverses the mapping specified by {@link #organize(int, int, int)}
     */
    public int unorganize(int index, int innerSize, int outerSize) {
        switch (this) {
            case EVEN:
                if (outerSize == 2) return 0;
                for (int z=0; z < innerSize; z++) {
                    if (organizeEven(z, innerSize, outerSize) == index) return z;
                }
                break;
            case RIGHT:
                return outerSize - 1 - index;
            case CENTER:
                return Math.floorDiv(outerSize - innerSize, 2) + innerSize - 1 - index;
        }
        return index;
    }

    public char getChar() {
        switch (this) {
            case LEFT:
                return '[';
            case RIGHT:
                return ']';
            case CENTER:
                return 'o';
            case EVEN:
                return '-';
            default:
                return '?';
        }
    }

}
