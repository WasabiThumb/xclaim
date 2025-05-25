package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.EditorConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlEditorConfig extends TomlConfig implements EditorConfig {

    public TomlEditorConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Boolean startOnCreate() {
        return this.getBoolean("start-on-create");
    }

    @Override
    public @UnknownNullability Boolean stopOnShutdown() {
        return this.getBoolean("stop-on-shutdown");
    }

    @Override
    public @UnknownNullability Boolean stopOnLeave() {
        return this.getBoolean("stop-on-leave");
    }

}
