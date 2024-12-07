package io.github.wasabithumb.xclaim.config.impl.yaml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.ProtectionConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public final class YamlProtectionConfig extends YamlConfig implements ProtectionConfig {

    public YamlProtectionConfig(@Nullable ConfigurationSection section) {
        super(section);
    }

}
