package codes.wasabi.xclaim.config.struct;

import codes.wasabi.xclaim.config.struct.sub.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface RootConfig extends Config {

    @UnknownNullability String language();

    @UnknownNullability Long veteranTime();

    @UnknownNullability Boolean noPaperNag();

    @Contract(pure = true)
    @NotNull AutoSaveConfig autoSave();

    @Contract(pure = true)
    @NotNull EditorConfig editor();

    @Contract(pure = true)
    @NotNull RulesConfig rules();

    @Contract(pure = true)
    @NotNull WorldsConfig worlds();

    @Contract(pure = true)
    @NotNull IntegrationsConfig integrations();

    default boolean isLegacy() {
        return false;
    }

}
