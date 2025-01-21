package io.github.wasabithumb.xclaim.config.impl.defaulting.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.filter.sub.integrations.FilterMapConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.MapConfig;
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
