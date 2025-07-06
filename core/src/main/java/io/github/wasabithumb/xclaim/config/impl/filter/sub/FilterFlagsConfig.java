package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterFlagsConfig extends FilterConfig implements FlagsConfig {

    public FilterFlagsConfig(@NotNull FlagsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull FlagsConfig backing() {
        return (FlagsConfig) super.backing();
    }

}
