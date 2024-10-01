package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import org.jetbrains.annotations.UnknownNullability;

public interface EditorConfig extends Config {

    @UnknownNullability Boolean startOnCreate();

    @UnknownNullability Boolean stopOnShutdown();

    @UnknownNullability Boolean stopOnLeave();

}
