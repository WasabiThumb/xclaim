package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.RulesConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterRulesConfig extends FilterConfig implements RulesConfig {

    public FilterRulesConfig(@NotNull RulesConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull RulesConfig backing() {
        return (RulesConfig) super.backing();
    }

}
