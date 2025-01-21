package io.github.wasabithumb.xclaim.config.impl.toml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.ProtectionConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;

public final class TomlProtectionConfig extends TomlConfig implements ProtectionConfig {

    public TomlProtectionConfig(@Nullable Toml table) {
        super(table);
    }

}
