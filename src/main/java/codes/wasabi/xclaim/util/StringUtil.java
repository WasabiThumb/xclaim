package codes.wasabi.xclaim.util;

import org.jetbrains.annotations.Range;

import java.nio.CharBuffer;

public final class StringUtil {

    public static String repeatString(String string, @Range(from=0L, to=Integer.MAX_VALUE) int count) {
        CharBuffer buffer = CharBuffer.allocate(string.length() * count);
        for (int i=0; i < count; i++) {
            buffer.put(string);
        }
        return buffer.toString();
    }

}
