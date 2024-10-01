package codes.wasabi.xclaim.config.impl.yaml.sub.integrations;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.ProtectionConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public final class YamlProtectionConfig extends YamlConfig implements ProtectionConfig {

    public YamlProtectionConfig(@Nullable ConfigurationSection section) {
        super(section);
    }

}
