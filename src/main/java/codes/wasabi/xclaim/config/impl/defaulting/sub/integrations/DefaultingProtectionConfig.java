package codes.wasabi.xclaim.config.impl.defaulting.sub.integrations;

import codes.wasabi.xclaim.config.impl.filter.sub.integrations.FilterProtectionConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.ProtectionConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingProtectionConfig extends FilterProtectionConfig {

    public DefaultingProtectionConfig(@NotNull ProtectionConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Boolean enabled() {
        return this.nullFallback(this.backing().enabled(), true);
    }

    @Override
    public @NotNull Boolean debug() {
        return this.nullFallback(this.backing().debug(), false);
    }

}
