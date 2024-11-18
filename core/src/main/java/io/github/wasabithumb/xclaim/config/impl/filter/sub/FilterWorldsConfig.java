package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.WorldsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterWorldsConfig extends FilterConfig implements WorldsConfig {

    public FilterWorldsConfig(@NotNull WorldsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull WorldsConfig backing() {
        return (WorldsConfig) super.backing();
    }

}
