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

}
