package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.AutoSaveConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterAutoSaveConfig extends FilterConfig implements AutoSaveConfig {

    public FilterAutoSaveConfig(@NotNull AutoSaveConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull AutoSaveConfig backing() {
        return (AutoSaveConfig) super.backing();
    }

}
