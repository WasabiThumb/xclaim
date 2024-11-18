package io.github.wasabithumb.xclaim.config.struct.sub;

import io.github.wasabithumb.xclaim.config.struct.helpers.ToggleableConfig;
import org.jetbrains.annotations.UnknownNullability;

public interface AutoSaveConfig extends ToggleableConfig {

    @UnknownNullability Long interval();

    @UnknownNullability Boolean silent();

}
