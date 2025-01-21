package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.EditorConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlEditorConfig extends TomlConfig implements EditorConfig {

    public TomlEditorConfig(@Nullable Toml table) {
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
