package codes.wasabi.xclaim.config.impl.filter.sub.integrations;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.MapConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterMapConfig extends FilterConfig implements MapConfig {

    public FilterMapConfig(@NotNull MapConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull MapConfig backing() {
        return (MapConfig) super.backing();
    }

}
