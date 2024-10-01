package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.WorldsConfig;
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
