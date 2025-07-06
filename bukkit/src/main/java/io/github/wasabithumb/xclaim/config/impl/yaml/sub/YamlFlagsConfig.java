package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlFlagsConfig extends YamlConfig implements FlagsConfig {

    public YamlFlagsConfig() {
        super(null);
    }

    @Override
    public @UnknownNullability Boolean defaultValue(@NotNull ClaimFlag flag) {
        return null;
    }

    @Override
    public @UnknownNullability Boolean configurable(@NotNull ClaimFlag flag) {
        return null;
    }

}
