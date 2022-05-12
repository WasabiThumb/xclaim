package codes.wasabi.xclaim.util;

public final class IntLongConverter {

    public static long intToLong(int a, int b) {
        return (((long) a) << 32) | (b & 0xFFFFFFFFL);
    }

    public static int[] longToInt(long l) {
        int a = (int) (l >> 32);
        int b = (int) l;
        return new int[]{ a, b };
    }

}
