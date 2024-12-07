package io.github.wasabithumb.xclaim.asset;

import org.jetbrains.annotations.NotNull;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public sealed interface AssetPath extends CharSequence {

    static @NotNull AssetPath of(@NotNull String @NotNull ... parts) {
        return new ByParts(Arrays.asList(parts));
    }

    static @NotNull AssetPath parse(@NotNull String path) {
        int len = path.length();
        BitSet sep = new BitSet(len);
        int sepCount = 0;
        boolean canBeLiteral = true;
        boolean trim = false;

        char c;
        for (int i=0; i < len; i++) {
            c = path.charAt(i);
            if (c == '/') {
                if (i == 0 || i == (len - 1)) {
                    canBeLiteral = false;
                    trim = true;
                }
            } else if (c == '\\') {
                canBeLiteral = false;
            } else {
                continue;
            }
            sep.set(i);
            sepCount++;
        }

        Literal literal = new Literal(path, sep, sepCount);
        if (canBeLiteral) return literal;

        List<CharSequence> parts = literal.parts();
        if (trim) {
            int start = 0;
            int end = parts.size();
            if (end != 0 && parts.getFirst().isEmpty()) start++;
            if ((end - start) > 0 && parts.getLast().isEmpty()) end--;
            parts = parts.subList(start, end);
        }
        return new ByParts(parts);
    }

    //

    @NotNull List<CharSequence> parts();

    @Override
    @NotNull String toString();

    //

    final class ByParts implements AssetPath {

        private final List<CharSequence> parts;
        private final int length;

        ByParts(@NotNull List<CharSequence> parts) {
            this.parts = Collections.unmodifiableList(parts);
            int length = 0;
            for (int i=0; i < parts.size(); i++) {
                if (i != 0) length++;
                length += parts.get(i).length();
            }
            this.length = length;
        }

        @Override
        public @NotNull List<CharSequence> parts() {
            return this.parts;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public char charAt(int i) {
            if (i < 0 || i >= this.length) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds for length " + this.length);
            }
            int len;
            for (CharSequence part : this.parts) {
                len = part.length();
                if (i < len) return part.charAt(i);
                i -= len;
                if (i == 0) return '/';
                i--;
            }
            throw new AssertionError();
        }

        @Override
        public @NotNull CharSequence subSequence(int i, int i1) {
            return CharBuffer.wrap(this)
                    .subSequence(i, i1);
        }

        @Override
        public @NotNull String toString() {
            StringBuilder sb = new StringBuilder(this.length);
            for (int i=0; i < this.parts.size(); i++) {
                if (i != 0) sb.append('/');
                sb.append(this.parts.get(i));
            }
            return sb.toString();
        }

    }

    final class Literal implements AssetPath {

        private final String value;
        private final BitSet sep;
        private final int sepCount;
        Literal(@NotNull String value, @NotNull BitSet sep, int sepCount) {
            this.value = value;
            this.sep = sep;
            this.sepCount = sepCount;
        }

        @Override
        public @NotNull List<CharSequence> parts() {
            if (this.sepCount == 0)
                return Collections.singletonList(this.value);

            CharSequence[] ret = new CharSequence[this.sepCount + 1];
            int head = 0;

            CharBuffer buf = CharBuffer.wrap(this.value);
            int start = 0;
            int end;

            while ((end = this.sep.nextSetBit(start)) != -1) {
                ret[head++] = buf.subSequence(start, end);
                start = end + 1;
            }
            ret[head] = buf.subSequence(start, buf.length());

            //noinspection Java9CollectionFactory
            return Collections.unmodifiableList(Arrays.asList(ret));
        }

        @Override
        public int length() {
            return this.value.length();
        }

        @Override
        public char charAt(int i) {
            return this.value.charAt(i);
        }

        @Override
        public @NotNull CharSequence subSequence(int i, int i1) {
            return this.value.subSequence(i, i1);
        }

        @Override
        public @NotNull String toString() {
            return this.value;
        }

    }

}
