package codes.wasabi.xclaim.config.impl.filter;

import codes.wasabi.xclaim.config.struct.RootConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterRootConfig extends FilterConfig implements RootConfig {

    public FilterRootConfig(@NotNull RootConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull RootConfig backing() {
        return (RootConfig) super.backing();
    }

}
