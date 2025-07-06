package io.github.wasabithumb.xclaim.claim.permission;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PermissionSet extends AbstractSet<Permission> {

    private static final int COUNT;
    static {
        if ((COUNT = PermissionImpl.count()) > 32)
            throw new AssertionError("Expectation failed (more than 32 constants)");
    }

    //

    private int data;

    public PermissionSet() {
        this.data = 0;
    }

    public PermissionSet(@NotNull Collection<? extends Permission> other) {
        if (other instanceof PermissionSet ps) {
            this.data = ps.data;
        } else {
            this.data = 0;
            for (Permission p : other) {
                this.data |= (1 << p.ordinal());
            }
        }
    }

    //

    @Override
    public int size() {
        return Integer.bitCount(this.data);
    }

    private boolean contains0(int ordinal) {
        return (this.data & (1 << ordinal)) != 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Permission perm)) return false;
        return this.contains0(perm.ordinal());
    }

    @Override
    public void clear() {
        this.data = 0;
    }

    @Override
    public boolean add(Permission permission) {
        if (permission == null) throw new NullPointerException("Cannot add null to PermissionSet");
        final int f = 1 << permission.ordinal();
        if ((this.data & f) == 0) {
            this.data |= f;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Permission perm)) return false;
        final int f = 1 << perm.ordinal();
        if ((this.data & f) != 0) {
            this.data ^= f;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @NotNull Iterator<Permission> iterator() {
        return new Iter(this);
    }

    //

    private static final class Iter implements Iterator<Permission> {

        private final PermissionSet parent;
        private int head;
        private int removable;

        Iter(@NotNull PermissionSet parent) {
            this.parent = parent;
            this.head = Integer.numberOfTrailingZeros(parent.data);
            this.removable = -1;
        }

        @Override
        public boolean hasNext() {
            return this.head < COUNT;
        }

        @Override
        public @NotNull Permission next() throws NoSuchElementException {
            if (this.head >= COUNT) throw new NoSuchElementException();
            final Permission ret = PermissionImpl.valueOf(this.head);
            this.removable = this.head;
            do {
                this.head++;
            } while (this.head < COUNT && !this.parent.contains0(this.head));
            return ret;
        }

        @Override
        public void remove() {
            if (this.removable == -1) throw new IllegalStateException("No removable element");
            this.parent.data &= (~(1 << this.removable));
            this.removable = -1;
        }
    }

}
