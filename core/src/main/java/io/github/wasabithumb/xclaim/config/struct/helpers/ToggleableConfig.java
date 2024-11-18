package io.github.wasabithumb.xclaim.config.struct.helpers;

import io.github.wasabithumb.xclaim.config.struct.Config;
import org.jetbrains.annotations.UnknownNullability;

public interface ToggleableConfig extends Config {

    default @UnknownNullability Boolean enabled() {
        return this.getBoolean("enabled");
    }

    default @UnknownNullability Boolean debug() {
        return this.getBoolean("debug");
    }

}
