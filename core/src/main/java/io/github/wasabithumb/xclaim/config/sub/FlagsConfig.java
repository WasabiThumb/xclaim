package io.github.wasabithumb.xclaim.config.sub;

import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface FlagsConfig extends Config {

    @UnknownNullability Boolean defaultValue(@NotNull ClaimFlag flag);

    @UnknownNullability Boolean configurable(@NotNull ClaimFlag flag);

}
