package io.github.wasabithumb.xclaim.config.impl.filter;

import io.github.wasabithumb.xclaim.config.RootConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterRootConfig extends FilterConfig implements RootConfig {

    public FilterRootConfig(@NotNull RootConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull RootConfig backing() {
        return (RootConfig) super.backing();
    }

    @Override
    public boolean isLegacy() {
        return this.backing().isLegacy();
    }

}
