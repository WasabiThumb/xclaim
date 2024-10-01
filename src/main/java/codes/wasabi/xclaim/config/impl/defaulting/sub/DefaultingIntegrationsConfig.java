package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.defaulting.sub.integrations.*;
import codes.wasabi.xclaim.config.impl.filter.sub.FilterIntegrationsConfig;
import codes.wasabi.xclaim.config.struct.sub.IntegrationsConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingIntegrationsConfig extends FilterIntegrationsConfig {

    private final DefaultingEconomyConfig economy;
    private final DefaultingMapConfig map;
    private final DefaultingProtectionConfig protection;
    public DefaultingIntegrationsConfig(@NotNull IntegrationsConfig backing) {
        super(backing);
        this.economy = new DefaultingEconomyConfig(backing.economy());
        this.map = new DefaultingMapConfig(backing.map());
        this.protection = new DefaultingProtectionConfig(backing.protection());
    }

    @Override
    public @NotNull DefaultingEconomyConfig economy() {
        return this.economy;
    }

    @Override
    public @NotNull DefaultingMapConfig map() {
        return this.map;
    }

    @Override
    public @NotNull DefaultingProtectionConfig protection() {
        return this.protection;
    }

}
