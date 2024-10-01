package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.IntegrationsConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterIntegrationsConfig extends FilterConfig implements IntegrationsConfig {

    public FilterIntegrationsConfig(@NotNull IntegrationsConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull IntegrationsConfig backing() {
        return (IntegrationsConfig) super.backing();
    }

}
