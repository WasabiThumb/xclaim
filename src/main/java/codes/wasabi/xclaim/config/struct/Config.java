package codes.wasabi.xclaim.config.struct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Config {

    @Nullable Config sub(@NotNull String key);

    @Nullable String getString(@NotNull String key);

    @Nullable Boolean getBoolean(@NotNull String key);

    @Nullable Integer getInt(@NotNull String key);

    @Nullable Long getLong(@NotNull String key);

}
