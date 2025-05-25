package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.AutoSaveConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlAutoSaveConfig extends TomlConfig implements AutoSaveConfig {

    public TomlAutoSaveConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Long interval() {
        return this.getLong("interval");
    }

    @Override
    public @UnknownNullability Boolean silent() {
        return this.getBoolean("silent");
    }

}
