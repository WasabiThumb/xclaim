package codes.wasabi.xclaim.util;

import org.jetbrains.annotations.Range;

public final class StringUtil {

    public static String repeatString(String string, @Range(from=0L, to=Integer.MAX_VALUE) int count) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < count; i++) {
            sb.append(string);
        }
        return sb.toString();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
