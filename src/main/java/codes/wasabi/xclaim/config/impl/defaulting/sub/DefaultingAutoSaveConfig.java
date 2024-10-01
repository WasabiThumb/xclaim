package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterAutoSaveConfig;
import codes.wasabi.xclaim.config.struct.sub.AutoSaveConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingAutoSaveConfig extends FilterAutoSaveConfig {

    public DefaultingAutoSaveConfig(@NotNull AutoSaveConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Boolean enabled() {
        return this.nullFallback(this.backing().enabled(), true);
    }

    @Override
    public @NotNull Boolean debug() {
        return this.nullFallback(this.backing().debug(), false);
    }

    @Override
    public @NotNull Long interval() {
        return this.nullFallback(this.backing().interval(), 300L);
    }

    @Override
    public @NotNull Boolean silent() {
        return this.nullFallback(this.backing().silent(), false);
    }

}
