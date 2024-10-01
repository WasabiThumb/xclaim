package codes.wasabi.xclaim.config.struct.helpers;

import codes.wasabi.xclaim.config.struct.Config;
import org.jetbrains.annotations.UnknownNullability;

public interface ToggleableConfig extends Config {

    default @UnknownNullability Boolean enabled() {
        return this.getBoolean("enabled");
    }

    default @UnknownNullability Boolean debug() {
        return this.getBoolean("debug");
    }

}
