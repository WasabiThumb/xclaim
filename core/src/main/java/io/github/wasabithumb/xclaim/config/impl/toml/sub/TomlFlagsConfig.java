package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.config.Config;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlFlagsConfig extends TomlConfig implements FlagsConfig {

    public TomlFlagsConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Boolean defaultValue(@NotNull ClaimFlag flag) {
        return this.withSub(flag.name(), (Config cfg) -> cfg.getBoolean("default"));
    }

    @Override
    public @UnknownNullability Boolean configurable(@NotNull ClaimFlag flag) {
        return this.withSub(flag.name(), (Config cfg) -> cfg.getBoolean("configurable"));
    }

}
