package io.github.wasabithumb.xclaim.config.sub;

import io.github.wasabithumb.xclaim.config.Config;
import org.jetbrains.annotations.UnknownNullability;

public interface EditorConfig extends Config {

    @UnknownNullability Boolean startOnCreate();

    @UnknownNullability Boolean stopOnShutdown();

    @UnknownNullability Boolean stopOnLeave();

}
