package codes.wasabi.xclaim.config.impl.toml.sub.integrations;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.ProtectionConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;

public final class TomlProtectionConfig extends TomlConfig implements ProtectionConfig {

    public TomlProtectionConfig(@Nullable Toml table) {
        super(table);
    }

}
