package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.GuiConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterGuiConfig extends FilterConfig implements GuiConfig {

    public FilterGuiConfig(@NotNull GuiConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull GuiConfig backing() {
        return (GuiConfig) super.backing();
    }

}
