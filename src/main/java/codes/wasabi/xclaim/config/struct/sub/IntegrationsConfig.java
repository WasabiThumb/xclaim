package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.MapConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.ProtectionConfig;
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
