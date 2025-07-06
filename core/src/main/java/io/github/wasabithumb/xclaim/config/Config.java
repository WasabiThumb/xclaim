package io.github.wasabithumb.xclaim.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Config {

    @Nullable Config sub(@NotNull String key);

    @Nullable String getString(@NotNull String key);

    @Nullable Boolean getBoolean(@NotNull String key);

    @Nullable Integer getInt(@NotNull String key);

    @Nullable Long getLong(@NotNull String key);

    default <T> @Nullable T withSub(@NotNull String key, @NotNull Function<Config, T> fn) {
        Config sub = this.sub(key);
        if (sub == null) return null;
        return fn.apply(sub);
    }

}
