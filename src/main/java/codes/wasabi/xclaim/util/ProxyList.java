package codes.wasabi.xclaim.util;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

public class ProxyList<E, T> extends AbstractList<T> {

    private final List<E> backing;
    private final Function<E, T> transformer;
    public ProxyList(List<E> backing, Function<E, T> transformer) {
        this.backing = backing;
        this.transformer = transformer;
    }

    @Override
    public T get(int i) {
        return this.transformer.apply(this.backing.get(i));
    }

    @Override
    public int size() {
        return this.backing.size();
    }

}
