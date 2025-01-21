package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.sub.AutoSaveConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlAutoSaveConfig extends YamlConfig implements AutoSaveConfig {

    public YamlAutoSaveConfig(@Nullable ConfigurationSection section) {
        super(section);
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
