package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.array.TomlArray;
import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.WorldsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public final class TomlWorldsConfig extends TomlConfig implements WorldsConfig {

    public TomlWorldsConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Long graceTime() {
        return this.getLong("grace-time");
    }

    @Override
    public @UnknownNullability Boolean useWhitelist() {
        return this.getBoolean("use-whitelist");
    }

    @Override
    public @UnknownNullability Collection<String> whitelist() {
        if (!this.valid || !Objects.equals(this.useWhitelist(), Boolean.TRUE)) return Collections.emptyList();
        return this.getStringList("whitelist");
    }

    @Override
    public @UnknownNullability Boolean useBlacklist() {
        return this.getBoolean("use-blacklist");
    }

    @Override
    public @UnknownNullability Collection<String> blacklist() {
        if (!this.valid || !Objects.equals(this.useBlacklist(), Boolean.TRUE)) return Collections.emptyList();
        return this.getStringList("blacklist");
    }

    @Override
    public @UnknownNullability Boolean caseSensitive() {
        return this.getBoolean("case-sensitive");
    }

    private @Nullable List<String> getStringList(@NotNull String key) {
        TomlValue tv = this.raw().get(key);
        if (tv == null || !tv.isArray()) return null;

        final TomlArray a = tv.asArray();
        return new AbstractList<>() {

            @Override
            public int size() {
                return a.size();
            }

            @Override
            public String get(int i) {
                TomlValue v = a.get(i);
                return v.isPrimitive() ?
                        v.asPrimitive().asString() :
                        v.toString();
            }

        };
    }

}
