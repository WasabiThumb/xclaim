package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.impl.toml.sub.integrations.*;
import io.github.wasabithumb.xclaim.config.sub.IntegrationsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TomlIntegrationsConfig extends TomlConfig implements IntegrationsConfig {

    private final TomlEconomyConfig economy;
    private final TomlMapConfig map;
    private final TomlProtectionConfig protection;
    public TomlIntegrationsConfig(@Nullable TomlTable table) {
        super(table);
        this.economy = new TomlEconomyConfig(this.getTable("economy"));
        this.map = new TomlMapConfig(this.getTable("map"));
        this.protection = new TomlProtectionConfig(this.getTable("protection"));
    }

    @Override
    public @NotNull TomlEconomyConfig economy() {
        return this.economy;
    }

    @Override
    public @NotNull TomlMapConfig map() {
        return this.map;
    }

    @Override
    public @NotNull TomlProtectionConfig protection() {
        return this.protection;
    }

}
