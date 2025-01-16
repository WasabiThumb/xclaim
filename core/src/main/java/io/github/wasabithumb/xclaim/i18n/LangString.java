package io.github.wasabithumb.xclaim.i18n;

import org.jetbrains.annotations.NotNull;

import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.List;

final class LangString {

    private static final char SPECIAL_CHAR = '$';
    private static final String SPECIAL = String.valueOf(SPECIAL_CHAR);

    static @NotNull LangString parse(@NotNull CharSequence data) {
        List<Part> parts = new LinkedList<>();
        int minLength = 0;
        CharBuffer buf = CharBuffer.wrap(data);
        char c;
        int a = 0;
        int num = -1;

        for (int i=0; i < data.length(); i++) {
            c = data.charAt(i);
            if (num != -1) {
                if ('0' <= c && c <= '9') {
                    num = (num * 10) + (c - '0');
                } else if (num == 0 && c == SPECIAL_CHAR) {
                    parts.add(new LiteralPart(SPECIAL));
                    minLength++;
                    a = i + 1;
                    num = -1;
                } else {
                    parts.add(new ArgPart(num));
                    a = i;
                    num = -1;
                }
            } else if (c == SPECIAL_CHAR) {
                if (i != a) {
                    parts.add(new LiteralPart(buf.subSequence(a, i)));
                    minLength += (i - a);
                }
                num = 0;
            }
        }
        if (num != -1) {
            parts.add(new ArgPart(num));
        } else if (a < data.length()) {
            parts.add(new LiteralPart(buf.subSequence(a, data.length())));
            minLength += (data.length() - a);
        }

        return new LangString(parts, minLength);
    }

    //

    private final List<Part> parts;
    private final int minLength;
    private LangString(@NotNull List<Part> parts, int minLength) {
        this.parts = parts;
        this.minLength = minLength;
    }

    @NotNull String resolve(@NotNull String @NotNull [] args) {
        StringBuilder ret = new StringBuilder(this.minLength);
        for (Part part : this.parts)
            ret.append(part.resolve(args));
        return ret.toString();
    }

    @NotNull String serialize() {
        StringBuilder ret = new StringBuilder(this.minLength);
        for (Part part : this.parts) {
            if (part instanceof LiteralPart literal) {
                ret.append(literal.value);
            } else if (part instanceof ArgPart arg) {
                ret.append('$').append(arg.index);
            }
        }
        return ret.toString();
    }

    //

    private sealed interface Part {

        @NotNull CharSequence resolve(@NotNull String @NotNull [] args);

    }

    private record LiteralPart(@NotNull CharSequence value) implements Part {

        @Override
        public @NotNull CharSequence resolve(@NotNull String @NotNull [] args) {
            return this.value;
        }

    }

    private record ArgPart(int index) implements Part {

        @Override
        public @NotNull CharSequence resolve(@NotNull String @NotNull [] args) {
            if (this.index < 1 || this.index > args.length) return "???";
            return args[this.index - 1];
        }

    }

}
