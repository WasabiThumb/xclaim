package codes.wasabi.xclaim.config.impl.filter.sub.integrations;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterEconomyConfig extends FilterConfig implements EconomyConfig {

    public FilterEconomyConfig(@NotNull EconomyConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull EconomyConfig backing() {
        return (EconomyConfig) super.backing();
    }

}
