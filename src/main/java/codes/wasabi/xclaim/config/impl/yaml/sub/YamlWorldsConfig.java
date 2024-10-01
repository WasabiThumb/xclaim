package codes.wasabi.xclaim.config.impl.yaml.sub;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.WorldsConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;

public final class YamlWorldsConfig extends YamlConfig implements WorldsConfig {

    public YamlWorldsConfig(@Nullable ConfigurationSection section) {
        super(section);
    }

    @Override
    public @UnknownNullability Long graceTime() {
        return this.getLong("grace-time");
    }

    @Override
    public @UnknownNullability Boolean useWhitelist() {
        return this.getBoolean("use-whitelist");
    }

    @Override
    public @UnknownNullability Collection<String> whitelist() {
        if (!this.valid) return Collections.emptyList();
        return this.raw().getStringList("whitelist");
    }

    @Override
    public @UnknownNullability Boolean useBlacklist() {
        return this.getBoolean("use-blacklist");
    }

    @Override
    public @UnknownNullability Collection<String> blacklist() {
        if (!this.valid) return Collections.emptyList();
        return this.raw().getStringList("blacklist");
    }

    @Override
    public @UnknownNullability Boolean caseSensitive() {
        return this.getBoolean("case-sensitive");
    }

}
