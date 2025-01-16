package io.github.wasabithumb.xclaim.placeholder;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.CharBuffer;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

@ApiStatus.Internal
public class PlaceholderArgumentQueue extends AbstractQueue<CharBuffer> {

    public static @NotNull PlaceholderArgumentQueue of(@NotNull String key) {
        return new PlaceholderArgumentQueue(CharBuffer.wrap(key));
    }

    //

    private final CharBuffer data;
    private int head;
    private int pollRewind;

    PlaceholderArgumentQueue(@NotNull CharBuffer data) {
        this.data = data;
        this.head = 0;
        this.pollRewind = -1;
    }

    //

    @Override
    public @NotNull Iterator<CharBuffer> iterator() {
        return new Iter(this);
    }

    @Override
    public int size() {
        int len = this.data.length();
        int size;
        if ((len - this.head) < 1) {
            size = 0;
        } else {
            size = 1;
            for (int i=this.head; i < (len - 1); i++) {
                if (this.data.charAt(i) == '_') size++;
            }
        }
        return size;
    }

    @Contract("_ -> false")
    @Override
    public boolean offer(CharBuffer charBuffer) {
        return false;
    }

    @Override
    public @Nullable CharBuffer poll() {
        return this.readAt(this.head, true);
    }

    @Override
    public @Nullable CharBuffer peek() {
        return this.readAt(this.head, false);
    }

    public @Nullable String pollString() {
        CharBuffer next = this.poll();
        if (next == null) return null;
        return next.toString();
    }

    public @Nullable String peekString() {
        CharBuffer next = this.peek();
        if (next == null) return null;
        return next.toString();
    }

    private @Nullable CharBuffer readAt(int start, boolean apply) {
        int end = this.data.length();
        int i = start;
        if (i >= end) return null;
        do {
            if (this.data.charAt(i) == '_') {
                end = i;
                break;
            }
            i++;
        } while (i < end);

        CharBuffer ret = this.data.subSequence(start, end);
        if (apply) {
            this.pollRewind = this.head;
            this.head = end + 1;
        }
        return ret;
    }

    void rewind() {
        if (this.pollRewind == -1)
            throw new IllegalStateException("Call to rewind() must follow call to poll()");
        this.head = this.pollRewind;
        this.pollRewind = -1;
    }

    //

    private static final class Iter implements Iterator<CharBuffer> {

        private final PlaceholderArgumentQueue parent;
        private int head;
        Iter(@NotNull PlaceholderArgumentQueue parent) {
            this.parent = parent;
            this.head = parent.head;
        }

        @Override
        public boolean hasNext() {
            return this.head < this.parent.data.length();
        }

        @Override
        public @NotNull CharBuffer next() {
            CharBuffer ret = this.parent.readAt(this.head, false);
            if (ret == null) throw new NoSuchElementException();
            this.head += ret.length() + 1;
            return ret;
        }

    }

}
