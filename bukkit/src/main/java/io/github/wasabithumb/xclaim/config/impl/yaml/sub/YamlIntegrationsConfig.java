package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.impl.yaml.helpers.YamlLimits;
import io.github.wasabithumb.xclaim.config.impl.yaml.sub.integrations.*;
import io.github.wasabithumb.xclaim.config.sub.IntegrationsConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class YamlIntegrationsConfig extends YamlConfig implements IntegrationsConfig {

    private final YamlEconomyConfig economy;
    private final YamlMapConfig map;
    private final YamlProtectionConfig protection;
    public YamlIntegrationsConfig(@Nullable ConfigurationSection section, @Nullable YamlLimits limits) {
        super(section);
        this.economy = new YamlEconomyConfig(section, limits);
        this.map = new YamlMapConfig(section);
        this.protection = new YamlProtectionConfig(section);
    }

    @Override
    public @NotNull YamlEconomyConfig economy() {
        return this.economy;
    }

    @Override
    public @NotNull YamlMapConfig map() {
        return this.map;
    }

    @Override
    public @NotNull YamlProtectionConfig protection() {
        return this.protection;
    }

}
