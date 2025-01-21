package io.github.wasabithumb.xclaim.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

public class ProxySet<E, T> extends AbstractSet<T> {

    private final Class<T> type;
    private final Set<E> backing;
    private final Function<E, T> transformer;
    private final Function<T, E> reverse;
    private final boolean hasReverse;

    public ProxySet(@NotNull Class<T> type, @NotNull Set<E> backing, @NotNull Function<E, T> transformer, @Nullable Function<T, E> reverse) {
        this.type = type;
        this.backing = backing;
        this.transformer = transformer;
        this.reverse = reverse;
        this.hasReverse = (reverse != null);
    }

    public ProxySet(@NotNull Class<T> type, @NotNull Set<E> backing, @NotNull Function<E, T> transformer) {
        this.type = type;
        this.backing = backing;
        this.transformer = transformer;
        this.reverse = null;
        this.hasReverse = false;
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.backing.stream()
                .map(this.transformer)
                .iterator();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return this.backing.contains(null);
        if (!this.type.isInstance(o)) return false;
        if (this.hasReverse) {
            assert this.reverse != null;
            return this.backing.contains(this.reverse.apply(this.type.cast(o)));
        }
        for (T value : this) {
            if (o.equals(value)) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Override
    public boolean add(T t) {
        if (this.hasReverse) {
            assert this.reverse != null;
            return this.backing.add(this.reverse.apply(t));
        }
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        if (this.hasReverse) {
            if (o != null && !this.type.isInstance(o)) return false;
            assert this.reverse != null;
            return this.backing.remove(this.reverse.apply(this.type.cast(o)));
        }
        return super.remove(o);
    }

}
