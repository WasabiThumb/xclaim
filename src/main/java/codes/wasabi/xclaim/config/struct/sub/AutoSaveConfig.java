package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.helpers.ToggleableConfig;
import org.jetbrains.annotations.UnknownNullability;

public interface AutoSaveConfig extends ToggleableConfig {

    @UnknownNullability Long interval();

    @UnknownNullability Boolean silent();

}
