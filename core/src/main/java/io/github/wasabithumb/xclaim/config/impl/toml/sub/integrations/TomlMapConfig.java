package io.github.wasabithumb.xclaim.config.impl.toml.sub.integrations;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.MapConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlMapConfig extends TomlConfig implements MapConfig {

    public TomlMapConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Boolean oldOutlineStyle() {
        return this.getBoolean("old-outline-style");
    }

}
