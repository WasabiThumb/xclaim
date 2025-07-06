package io.github.wasabithumb.xclaim.config;

import io.github.wasabithumb.xclaim.config.sub.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface RootConfig extends Config {

    @UnknownNullability String language();

    @UnknownNullability Long veteranTime();

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

    @Contract(pure = true)
    @NotNull GuiConfig gui();

    @Contract(pure = true)
    @NotNull PermissionsConfig permissions();

    @Contract(pure = true)
    @NotNull FlagsConfig flags();

    default boolean isLegacy() {
        return false;
    }

}
