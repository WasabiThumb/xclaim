package io.github.wasabithumb.xclaim.config.impl.toml.helpers;

import io.github.wasabithumb.jtoml.key.TomlKey;
import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Set;

public abstract class TomlGroupableValue<T> {

    private final int mode; // 0: no value, 1: grouped, 2: ungrouped (primitive)
    private final Object value; // either Toml or primitive, depending on mode
    private final Set<TomlKey> keys; // all groups specified, null if mode != 1

    public TomlGroupableValue(@Nullable TomlTable table, @NotNull TomlKey key) {
        int mode = 0;
        Object value = null;
        Set<TomlKey> keys = null;

        if (table != null) {
            if (table.get(key) instanceof TomlTable sub) {
                mode = 1;
                value = sub;
                keys = sub.keys();
            } else if ((value = this.extract(table, key)) != null) {
                mode = 2;
            }
        }

        this.mode = mode;
        this.value = value;
        this.keys = keys;
    }

    public TomlGroupableValue(@Nullable TomlTable table, @NotNull CharSequence key) {
        this(table, TomlKey.parse(key));
    }

    //

    protected abstract @Nullable T extract(@NotNull TomlTable table, @NotNull TomlKey key);

    @Contract("null -> null; !null -> !null")
    protected abstract T cast(Object object);

    protected abstract int compare(@NotNull T a, @NotNull T b);

    public @Nullable T get(@Nullable PlatformUser target) {
        if (this.mode == 1) {
            return this.getMode1(target, (TomlTable) this.value, this.keys);
        } else if (this.mode == 2) {
            return this.cast(this.value);
        }
        return null;
    }

    private @Nullable T getMode1(@Nullable PlatformUser target, @NotNull TomlTable table, @NotNull Set<TomlKey> keys) {
        T ret = null;
        boolean any = false;

        T next;
        for (TomlKey group : keys) {
            if (!this.inGroup(target, group.toString())) continue;

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

    private boolean inGroup(@Nullable PlatformUser target, @NotNull String group) {
        if (group.equals("default")) return true;
        if (target == null) return false;
        if (target.isOp()) return true;
        return target.hasPermission("xclaim.group." + group);
    }

    //

    public static final class Int extends TomlGroupableValue<Integer> {

        private final Comparator<Integer> comparator;
        public Int(@Nullable TomlTable table, @NotNull String key, @NotNull Comparator<Integer> comparator) {
            super(table, key);
            this.comparator = comparator;
        }

        @Override
        protected @Nullable Integer extract(@NotNull TomlTable table, @NotNull TomlKey key) {
            TomlValue tv = table.get(key);
            if (tv == null || !tv.isPrimitive()) return null;
            return tv.asPrimitive().asInteger();
        }

        @Override
        protected Integer cast(Object object) {
            return (Integer) object;
        }

        @Override
        protected int compare(@NotNull Integer a, @NotNull Integer b) {
            return this.comparator.compare(a, b);
        }

    }

}
