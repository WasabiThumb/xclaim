package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.NotNull;

public final class StringUtil {

    private static final String HEX_CHARS = "0123456789ABCDEF";
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARS.charAt(v >>> 4);
            hexChars[j * 2 + 1] = HEX_CHARS.charAt(v & 0x0F);
        }
        return new String(hexChars);
    }

    public static int distance(@NotNull CharSequence a, @NotNull CharSequence b) {
        int al = a.length();
        int bl = b.length();
        int[][] table = new int[al + 1][bl + 1];

        for (int i=0; i <= al; i++) {
            for (int j=0; j <= bl; j++) {
                if (i == 0) {
                    table[i][j] = j;
                    continue;
                } else if (j == 0) {
                    table[i][j] = i;
                    continue;
                }
                table[i][j] = distance0(
                        table[i - 1][j - 1] + distance0(a.charAt(i - 1), b.charAt(j - 1)),
                        table[i - 1][j] + 1,
                        table[i][j - 1] + 1
                );
            }
        }

        return table[al][bl];
    }

    private static int distance0(char a, char b) {
        return (a == b) ? 0 : 1;
    }

    private static int distance0(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

}
