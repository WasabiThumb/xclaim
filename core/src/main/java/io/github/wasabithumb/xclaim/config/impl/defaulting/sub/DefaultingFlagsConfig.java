package io.github.wasabithumb.xclaim.config.impl.defaulting.sub;

import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.config.impl.filter.sub.FilterFlagsConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DefaultingFlagsConfig extends FilterFlagsConfig {

    public DefaultingFlagsConfig(@NotNull FlagsConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Boolean defaultValue(@NotNull ClaimFlag flag) {
        return Objects.requireNonNullElse(this.backing().defaultValue(flag), Boolean.FALSE);
    }

    @Override
    public @NotNull Boolean configurable(@NotNull ClaimFlag flag) {
        return Objects.requireNonNullElse(this.backing().configurable(flag), Boolean.TRUE);
    }

}
