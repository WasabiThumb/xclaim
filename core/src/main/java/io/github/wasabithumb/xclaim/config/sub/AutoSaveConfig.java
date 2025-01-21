package io.github.wasabithumb.xclaim.config.sub;

import io.github.wasabithumb.xclaim.config.helpers.ToggleableConfig;
import org.jetbrains.annotations.UnknownNullability;

public interface AutoSaveConfig extends ToggleableConfig {

    @UnknownNullability Long interval();

    @UnknownNullability Boolean silent();

}
