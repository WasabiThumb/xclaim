package io.github.wasabithumb.xclaim.util;

public final class BitManipulation {

    public static long i32i64(int a, int b) {
        return (((long) a) << 32) | (b & 0xFFFFFFFFL);
    }

    public static int[] i64i32(long l) {
        int a = (int) (l >> 32);
        int b = (int) l;
        return new int[]{ a, b };
    }

}
