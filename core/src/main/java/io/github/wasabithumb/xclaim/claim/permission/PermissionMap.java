package io.github.wasabithumb.xclaim.claim.permission;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PermissionMap<V> extends AbstractMap<Permission, V> {

    private static final int COUNT = PermissionImpl.count();

    //

    private final Object[] values;
    private int size;

    public PermissionMap() {
        this.values = new Object[COUNT];
        this.size = 0;
    }

    public PermissionMap(@NotNull Map<? extends Permission, ? extends V> map) {
        if (map instanceof PermissionMap<?> pm) {
            this.values = Arrays.copyOf(pm.values, COUNT);
            this.size = pm.size;
        } else {
            this.values = new Object[COUNT];
            this.size = 0;
            this.putAll(map);
        }
    }

    //

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Permission perm)) return false;
        return this.values[perm.ordinal()] != null;
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof Permission perm)) return null;
        //noinspection unchecked
        return (V) this.values[perm.ordinal()];
    }

    @Override
    public V put(Permission key, V value) {
        if (key == null) throw new NullPointerException("PermissionMap does not support null keys");
        if (value == null) throw new NullPointerException("PermissionMap does not support null values");
        final int ordinal = key.ordinal();
        final Object old = this.values[ordinal];
        this.values[ordinal] = value;
        if (old == null) {
            this.size++;
            return null;
        } else {
            //noinspection unchecked
            return (V) old;
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof Permission perm)) return null;
        final int ordinal = perm.ordinal();
        final Object old = this.values[ordinal];
        this.values[ordinal] = null;
        if (old == null) {
            return null;
        } else {
            this.size--;
            //noinspection unchecked
            return (V) old;
        }
    }

    @Override
    public @NotNull Set<Entry<Permission, V>> entrySet() {
        return new Entries<>(this);
    }

    //

    private static final class Entries<V> extends AbstractSet<Entry<Permission, V>> {

        private final PermissionMap<V> parent;

        Entries(@NotNull PermissionMap<V> parent) {
            this.parent = parent;
        }

        //

        @Override
        public void clear() {
            this.parent.clear();
        }

        @Override
        public int size() {
            return this.parent.size;
        }

        @Override
        public @NotNull Iterator<Entry<Permission, V>> iterator() {
            return new EntryIterator<>(this.parent);
        }

    }

    private static final class EntryIterator<V> implements Iterator<Entry<Permission, V>> {

        private final PermissionMap<V> parent;
        private int head;
        private int removable;

        EntryIterator(@NotNull PermissionMap<V> parent) {
            this.parent = parent;
            this.head = 0;
            this.removable = -1;
        }

        //

        @Override
        public boolean hasNext() {
            while (this.head < COUNT) {
                if (this.parent.values[this.head] != null) return true;
                this.head++;
            }
            return false;
        }

        @Override
        public @NotNull Entry<Permission, V> next() throws NoSuchElementException {
            final int index = this.head;
            Object value;
            if (index >= COUNT || (value = this.parent.values[index]) == null)
                throw new NoSuchElementException();

            //noinspection unchecked
            final Entry<Permission, V> ret = Map.entry(Permission.valueOf(index), (V) value);
            this.head++;
            this.removable = index;
            return ret;
        }

        @Override
        public void remove() throws IllegalStateException {
            final int index = this.removable;
            if (index == -1) throw new IllegalStateException("No removable element");
            this.removable = -1;
            if (this.parent.values[index] == null) return;
            this.parent.values[index] = null;
            this.parent.size--;
        }

    }

}
