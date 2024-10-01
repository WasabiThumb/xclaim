package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterWorldsConfig;
import codes.wasabi.xclaim.config.struct.sub.WorldsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public final class DefaultingWorldsConfig extends FilterWorldsConfig {

    public DefaultingWorldsConfig(@NotNull WorldsConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Long graceTime() {
        return this.nullFallback(this.backing().graceTime(), 604800L);
    }

    @Override
    public @NotNull Boolean useWhitelist() {
        return this.nullFallback(this.backing().useWhitelist(), false);
    }

    @Override
    public @NotNull Collection<String> whitelist() {
        return this.nullFallback(this.backing().whitelist(), Collections.emptyList());
    }

    @Override
    public @NotNull Boolean useBlacklist() {
        return this.nullFallback(this.backing().useBlacklist(), false);
    }

    @Override
    public @NotNull Collection<String> blacklist() {
        return this.nullFallback(this.backing().blacklist(), Collections.emptyList());
    }

    @Override
    public @NotNull Boolean caseSensitive() {
        return this.nullFallback(this.backing().caseSensitive(), true);
    }

}
