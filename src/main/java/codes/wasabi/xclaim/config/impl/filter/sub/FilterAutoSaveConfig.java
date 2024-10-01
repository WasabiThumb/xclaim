package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.AutoSaveConfig;
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
