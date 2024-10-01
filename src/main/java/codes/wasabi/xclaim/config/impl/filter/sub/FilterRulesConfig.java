package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.RulesConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterRulesConfig extends FilterConfig implements RulesConfig {

    public FilterRulesConfig(@NotNull RulesConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull RulesConfig backing() {
        return (RulesConfig) super.backing();
    }

}
