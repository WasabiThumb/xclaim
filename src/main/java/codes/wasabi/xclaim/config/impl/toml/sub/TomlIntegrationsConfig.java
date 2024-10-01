package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.impl.toml.sub.integrations.*;
import codes.wasabi.xclaim.config.struct.sub.IntegrationsConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TomlIntegrationsConfig extends TomlConfig implements IntegrationsConfig {

    private final TomlEconomyConfig economy;
    private final TomlMapConfig map;
    private final TomlProtectionConfig protection;
    public TomlIntegrationsConfig(@Nullable Toml table) {
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
