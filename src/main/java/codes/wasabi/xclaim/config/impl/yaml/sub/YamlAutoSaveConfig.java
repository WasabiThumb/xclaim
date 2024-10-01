package codes.wasabi.xclaim.config.impl.yaml.sub;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.AutoSaveConfig;
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
