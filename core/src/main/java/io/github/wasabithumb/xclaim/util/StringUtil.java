package io.github.wasabithumb.xclaim.util;

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

}
