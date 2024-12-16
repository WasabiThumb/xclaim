package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.DefaultPermissionsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterDefaultPermissionsConfig extends FilterConfig implements DefaultPermissionsConfig {

    public FilterDefaultPermissionsConfig(@NotNull DefaultPermissionsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull DefaultPermissionsConfig backing() {
        return (DefaultPermissionsConfig) super.backing();
    }

}
