package codes.wasabi.xclaim.config.impl.toml.helpers;

import com.moandjiezana.toml.Toml;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class TomlGroupableValue<T> {

    private final int mode; // 0: no value, 1: grouped, 2: ungrouped (primitive)
    private final Object value; // either Toml or primitive, depending on mode
    private final Set<String> keys; // all groups specified, null if mode != 2

    public TomlGroupableValue(@Nullable Toml table, @NotNull String key) {
        int mode = 0;
        Object value = null;
        Set<String> keys = null;

        if (table != null) {
            Toml sub;
            if (table.containsTable(key) && (sub = table.getTable(key)) != null) {
                mode = 1;
                value = sub;

                Set<Map.Entry<String, Object>> entries = sub.entrySet();
                keys = new LinkedHashSet<>(entries.size());
                for (Map.Entry<String, Object> entry : entries) {
                    keys.add(entry.getKey());
                }
            } else if ((value = this.extract(table, key)) != null) {
                mode = 2;
            }
        }

        this.mode = mode;
        this.value = value;
        this.keys = keys;
    }

    protected abstract @Nullable T extract(@NotNull Toml table, @NotNull String key);

    @Contract("null -> null; !null -> !null")
    protected abstract T cast(Object object);

    protected abstract int compare(@NotNull T a, @NotNull T b);

    public @Nullable T get(@Nullable Permissible target) {
        if (this.mode == 1) {
            return this.getMode1(target, (Toml) this.value, this.keys);
        } else if (this.mode == 2) {
            return this.cast(this.value);
        }
        return null;
    }

    private @Nullable T getMode1(@Nullable Permissible target, @NotNull Toml table, @NotNull Set<String> keys) {
        T ret = null;
        boolean any = false;

        T next;
        for (String group : keys) {
            if (!this.inGroup(target, group)) continue;

            next = this.extract(table, group);
            if (next == null) continue;

            if (any) {
                if (this.compare(next, ret) > 0) {
                    ret = next;
                }
            } else {
                ret = next;
                any = true;
            }
        }

        return ret;
    }

    private boolean inGroup(@Nullable Permissible target, @NotNull String group) {
        if (group.equals("default")) return true;
        if (target == null) return false;
        if (target.isOp()) return true;
        return target.hasPermission("xclaim.group." + group);
    }

    //

    public static final class Int extends TomlGroupableValue<Integer> {

        private final IntComparator comparator;
        public Int(@Nullable Toml table, @NotNull String key, @NotNull IntComparator comparator) {
            super(table, key);
            this.comparator = comparator;
        }

        @Override
        protected @Nullable Integer extract(@NotNull Toml table, @NotNull String key) {
            Long value;
            try {
                value = table.getLong(key);
            } catch (ClassCastException ignored) {
                return null;
            }
            if (value == null) return null;
            try {
                return Math.toIntExact(value);
            } catch (ArithmeticException ignored) {
                return null;
            }
        }

        @Override
        protected Integer cast(Object object) {
            return (Integer) object;
        }

        @Override
        protected int compare(@NotNull Integer a, @NotNull Integer b) {
            return this.comparator.compare(a.intValue(), b.intValue());
        }

    }

}
