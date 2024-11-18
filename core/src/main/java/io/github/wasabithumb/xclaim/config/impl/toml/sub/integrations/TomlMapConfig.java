package io.github.wasabithumb.xclaim.config.impl.toml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.MapConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlMapConfig extends TomlConfig implements MapConfig {

    public TomlMapConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability Boolean oldOutlineStyle() {
        return this.getBoolean("old-outline-style");
    }

}
