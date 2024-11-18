package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.IntegrationsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterIntegrationsConfig extends FilterConfig implements IntegrationsConfig {

    public FilterIntegrationsConfig(@NotNull IntegrationsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull IntegrationsConfig backing() {
        return (IntegrationsConfig) super.backing();
    }

}
