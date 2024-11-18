package io.github.wasabithumb.xclaim.config.impl.filter.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.ProtectionConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterProtectionConfig extends FilterConfig implements ProtectionConfig {

    public FilterProtectionConfig(@NotNull ProtectionConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull ProtectionConfig backing() {
        return (ProtectionConfig) super.backing();
    }

}
