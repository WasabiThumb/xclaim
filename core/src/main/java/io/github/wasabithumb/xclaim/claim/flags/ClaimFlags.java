package io.github.wasabithumb.xclaim.claim.flags;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ClaimFlags extends AbstractSet<ClaimFlag> {

    public static @NotNull ClaimFlags fromString(@NotNull String s) throws IllegalArgumentException {
        ClaimFlags ret = new ClaimFlags();
        ClaimFlag next;
        for (int i=0; i < s.length(); i++) {
            next = ClaimFlagImpl.byChar(s.charAt(i));
            ret.value |= next.value();
        }
        return ret;
    }

    //

    private int value;

    public ClaimFlags() {
        this.value = 0;
    }

    public ClaimFlags(@NotNull Collection<? extends ClaimFlag> src) {
        this.set(src);
    }

    //

    public void set(@NotNull Collection<? extends ClaimFlag> other) {
        if (other instanceof ClaimFlags pure) {
            this.value = pure.value;
        } else {
            int v = 0;
            for (ClaimFlag cf : other) v |= cf.value();
            this.value = v;
        }
    }

    @Override
    public int size() {
        return Integer.bitCount(this.value);
    }

    @Override
    @Contract("null -> false")
    public boolean contains(Object o) {
        if (!(o instanceof ClaimFlag flag)) return false;
        return (this.value & flag.value()) != 0;
    }

    @Override
    @Contract("null -> fail")
    public boolean add(ClaimFlag claimFlag) {
        if (claimFlag == null) throw new NullPointerException("Cannot add null to ClaimFlags");
        final int v = claimFlag.value();
        if ((this.value & v) == 0) {
            this.value |= v;
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Contract("null -> false")
    public boolean remove(Object o) {
        if (!(o instanceof ClaimFlag flag)) return false;
        final int v = flag.value();
        if ((this.value & v) != 0) {
            this.value ^= v;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends ClaimFlag> c) {
        if (c instanceof ClaimFlags other) {
            final int nv = this.value | other.value;
            if (this.value == nv) return false;
            this.value = nv;
            return true;
        } else {
            return super.addAll(c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c instanceof ClaimFlags other) {
            final int nv = this.value & (~other.value);
            if (this.value == nv) return false;
            this.value = nv;
            return true;
        } else {
            return super.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if (c instanceof ClaimFlags other) {
            final int nv = this.value & other.value;
            if (this.value == nv) return false;
            this.value = nv;
            return true;
        } else {
            return super.retainAll(c);
        }
    }

    @Override
    public void clear() {
        this.value = 0;
    }

    @Override
    public @NotNull Iterator<ClaimFlag> iterator() {
        return new Iter(this);
    }

    @Override
    public @NotNull String toString() {
        final int size = this.size();
        final char[] buf = new char[size];
        final Iter iter = new Iter(this);
        for (int i=0; i < size; i++) {
            buf[i] = iter.next().magic();
        }
        return new String(buf);
    }

    //

    private static final class Iter implements Iterator<ClaimFlag> {

        private final ClaimFlags parent;
        private int head;
        private int removable;

        Iter(@NotNull ClaimFlags parent) {
            this.parent = parent;
            this.head = Integer.numberOfTrailingZeros(parent.value);
            this.removable = -1;
        }

        //

        @Override
        public boolean hasNext() {
            return this.head < ClaimFlagImpl.SHIFT_MAX;
        }

        @Override
        public @NotNull ClaimFlag next() throws NoSuchElementException {
            if (this.head >= ClaimFlagImpl.SHIFT_MAX) throw new NoSuchElementException();
            final ClaimFlag ret = ClaimFlagImpl.byShift(this.head);
            this.removable = ret.value();
            do {
                this.head++;
            } while (this.head < ClaimFlagImpl.SHIFT_MAX && (this.parent.value & (1 << this.head)) == 0);
            return ret;
        }

        @Override
        public void remove() {
            if (this.removable == -1) throw new IllegalStateException("No removable element");
            this.parent.value &= (~this.removable);
            this.removable = -1;
        }

    }

}
