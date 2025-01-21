package io.github.wasabithumb.xclaim.config.sub;

import io.github.wasabithumb.xclaim.config.Config;
import io.github.wasabithumb.xclaim.config.sub.integrations.EconomyConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.MapConfig;
import io.github.wasabithumb.xclaim.config.sub.integrations.ProtectionConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface IntegrationsConfig extends Config {

    @Contract(pure = true)
    @NotNull EconomyConfig economy();

    @Contract(pure = true)
    @NotNull MapConfig map();

    @Contract(pure = true)
    @NotNull ProtectionConfig protection();

}
