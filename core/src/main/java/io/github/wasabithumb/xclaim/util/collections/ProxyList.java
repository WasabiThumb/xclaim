package io.github.wasabithumb.xclaim.util.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

public class ProxyList<E, T> extends AbstractList<T> {

    private final List<E> backing;
    private final Function<E, T> transformer;
    private final Function<T, E> inverse;
    private final boolean mutable;

    public ProxyList(@NotNull List<E> backing, @NotNull Function<E, T> transformer, @Nullable Function<T, E> inverse) {
        this.backing = backing;
        this.transformer = transformer;
        this.inverse = inverse;
        this.mutable = inverse != null;
    }

    public ProxyList(@NotNull List<E> backing, @NotNull Function<E, T> transformer) {
        this.backing = backing;
        this.transformer = transformer;
        this.inverse = null;
        this.mutable = false;
    }

    @Override
    public T get(int i) {
        return this.transformer.apply(this.backing.get(i));
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public void add(int index, T element) {
        this.assertMutable();
        assert this.inverse != null;
        this.backing.add(index, this.inverse.apply(element));
    }

    @Override
    public T set(int index, T element) {
        this.assertMutable();
        assert this.inverse != null;
        E ret = this.backing.set(index, this.inverse.apply(element));
        if (ret == null) return null;
        return this.transformer.apply(ret);
    }

    @Override
    public T remove(int index) {
        this.assertMutable();
        E ret = this.backing.remove(index);
        if (ret == null) return null;
        return this.transformer.apply(ret);
    }

    @Override
    public void clear() {
        this.assertMutable();
        this.backing.clear();
    }

    private void assertMutable() throws UnsupportedOperationException {
        if (!this.mutable) throw new UnsupportedOperationException("Cannot mutate ProxyList (no inverse transformer supplied)");
    }

}
