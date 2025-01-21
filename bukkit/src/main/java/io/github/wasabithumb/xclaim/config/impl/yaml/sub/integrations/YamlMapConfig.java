package io.github.wasabithumb.xclaim.config.impl.yaml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.MapConfig;
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
