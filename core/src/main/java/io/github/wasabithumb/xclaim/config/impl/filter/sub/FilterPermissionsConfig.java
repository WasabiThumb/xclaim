package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterPermissionsConfig extends FilterConfig implements PermissionsConfig {

    public FilterPermissionsConfig(@NotNull PermissionsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull PermissionsConfig backing() {
        return (PermissionsConfig) super.backing();
    }

}
