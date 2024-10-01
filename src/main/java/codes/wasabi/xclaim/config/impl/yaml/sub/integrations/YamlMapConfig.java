package codes.wasabi.xclaim.config.impl.yaml.sub.integrations;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.MapConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlMapConfig extends YamlConfig implements MapConfig {

    public YamlMapConfig(@Nullable ConfigurationSection section) {
        super(section);
    }

    @Override
    public @UnknownNullability Boolean oldOutlineStyle() {
        return this.getBoolean("use-old-outline-style");
    }

}
