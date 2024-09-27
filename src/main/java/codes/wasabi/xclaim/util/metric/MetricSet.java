package codes.wasabi.xclaim.util.metric;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MetricSet extends AbstractSet<IMetric> {

    private static final MetricSet ALL = of(Metric.values());
    public static @NotNull MetricSet all() {
        return ALL;
    }

    public static @NotNull MetricSet of(@NotNull Iterable<IMetric> iter) throws IllegalArgumentException {
        int min;
        int max;
        IMetric tmp;
        int len = 1;

        Iterator<IMetric> ia = iter.iterator();
        if (!ia.hasNext()) throw new IllegalArgumentException("MetricSet cannot be empty");
        min = max = ia.next().magnitude();

        while (ia.hasNext()) {
            tmp = ia.next();
            if (tmp.magnitude() < min) {
                min = tmp.magnitude();
            } else if (tmp.magnitude() > max) {
                max = tmp.magnitude();
            }
            len++;
        }

        return new MetricSet(iter, min, max, len);
    }

    public static @NotNull MetricSet of(@NotNull IMetric @NotNull ... metrics) throws IllegalArgumentException {
        return of(Arrays.asList(metrics));
    }

    @Contract(" -> new")
    public static @NotNull MetricSet.Builder builder() {
        return new MetricSet.Builder();
    }

    //

    protected final int root;
    protected final IMetric[] universe;
    protected final int len;

    MetricSet(Iterable<IMetric> iter, int min, int max, int len) throws IllegalArgumentException {
        final int universeLen = max - min + 1;
        IMetric[] universe = new IMetric[universeLen];
        int dest;
        for (IMetric m : iter) {
            dest = m.magnitude() - min;
            if (dest < 0 || dest >= universeLen) throw new ConcurrentModificationException();
            if (universe[dest] != null)
                throw new IllegalArgumentException("Metrics passed to MetricSet have multiple Metrics with the same magnitude (" + m.magnitude() + ")");
            universe[dest] = m;
        }

        this.root = min;
        this.universe = universe;
        this.len = len;
    }

    //

    /**
     * Clamps the magnitude within the set, rounds down towards 0, and returns the Metric within this set that can best
     * format a value of the specified magnitude.
     */
    public @NotNull IMetric getByMagnitude(int magnitude) {
        magnitude -= this.root;
        if (magnitude <= 0) return this.universe[0];

        int i = Math.min(magnitude, this.universe.length - 1);
        IMetric ret;
        for (; i >= 0; i--) {
            ret = this.universe[i];
            if (ret != null) return ret;
        }

        throw new AssertionError("Malformed universe");
    }

    public @Nullable IMetric getByMagnitudeExact(int magnitude) {
        magnitude -= this.root;
        if (magnitude < 0) return null;
        if (magnitude >= this.universe.length) return null;
        return this.universe[magnitude];
    }

    @Override
    public int size() {
        return this.len;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        if (!(o instanceof IMetric)) return false;
        return this.getByMagnitudeExact(((IMetric) o).magnitude()) != null;
    }

    @Override
    public @NotNull Iterator<IMetric> iterator() {
        return new Iter(this);
    }

    //

    private static final class Iter implements Iterator<IMetric> {

        private final MetricSet parent;
        private int head = 0;
        Iter(MetricSet parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return this.head < this.parent.universe.length;
        }

        @Override
        public @NotNull IMetric next() throws NoSuchElementException {
            IMetric ret;
            while (this.head < this.parent.universe.length) {
                ret = this.parent.universe[this.head++];
                if (ret != null) return ret;
            }
            throw new NoSuchElementException();
        }

    }

    //

    public static final class Builder {

        private List<IMetric> list = new LinkedList<>();
        private boolean linked = true;
        private int min = 0;
        private int max = 0;

        private void makeArray(int capacityHint) {
            if (this.linked) {
                List<IMetric> cpy = new ArrayList<>(this.list.size() + capacityHint);
                cpy.addAll(this.list);
                this.list = cpy;
                this.linked = false;
            }
        }

        @Contract("_ -> this")
        public @NotNull MetricSet.Builder add(@NotNull IMetric metric) {
            final int mag = metric.magnitude();
            if (this.list.isEmpty()) {
                this.min = this.max = mag;
            } else if (mag < this.min) {
                this.min = mag;
            } else if (mag > this.max) {
                this.max = mag;
            }
            this.list.add(metric);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull MetricSet.Builder add(@NotNull Iterable<IMetric> metrics) {
            this.makeArray(0);
            for (IMetric metric : metrics) this.add(metric);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull MetricSet.Builder add(@NotNull Collection<IMetric> metrics) {
            this.makeArray(metrics.size());
            for (IMetric metric : metrics) this.add(metric);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull MetricSet.Builder add(@NotNull IMetric @NotNull ... metrics) {
            this.makeArray(metrics.length);
            for (IMetric metric : metrics) this.add(metric);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull MetricSet.Builder upTo(@NotNull Metric metric) {
            Metric[] values = Metric.values();
            this.makeArray(values.length);
            for (Metric value : values) {
                this.add(value);
                if (value == metric) break;
            }
            return this;
        }

        @Contract(" -> new")
        public @NotNull MetricSet build() throws IllegalStateException {
            final int len = this.list.size();
            if (len == 0) throw new IllegalStateException("MetricSet cannot be empty");
            return new MetricSet(this.list, this.min, this.max, len);
        }

    }

}
