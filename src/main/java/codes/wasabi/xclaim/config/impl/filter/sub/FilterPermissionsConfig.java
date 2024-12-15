package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.PermissionsConfig;
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
