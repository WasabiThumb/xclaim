package io.github.wasabithumb.xclaim.config.impl.filter.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.EconomyConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterEconomyConfig extends FilterConfig implements EconomyConfig {

    public FilterEconomyConfig(@NotNull EconomyConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull EconomyConfig backing() {
        return (EconomyConfig) super.backing();
    }

}
