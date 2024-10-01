package codes.wasabi.xclaim.config.impl.defaulting.sub.integrations;

import codes.wasabi.xclaim.config.impl.filter.sub.integrations.FilterMapConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.MapConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingMapConfig extends FilterMapConfig {

    public DefaultingMapConfig(@NotNull MapConfig backing) {
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

    @Override
    public @NotNull Boolean oldOutlineStyle() {
        return this.nullFallback(this.backing().oldOutlineStyle(), false);
    }

}
