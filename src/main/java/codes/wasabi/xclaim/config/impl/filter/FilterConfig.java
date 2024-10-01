package codes.wasabi.xclaim.config.impl.filter;

import codes.wasabi.xclaim.config.struct.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilterConfig implements Config {

    private final Config backing;
    public FilterConfig(@NotNull Config backing) {
        this.backing = backing;
    }

    protected @NotNull Config backing() {
        return this.backing;
    }

    @Override
    public @Nullable Config sub(@NotNull String key) {
        return this.backing.sub(key);
    }

    @Override
    public @Nullable String getString(@NotNull String key) {
        return this.backing.getString(key);
    }

    @Override
    public @Nullable Boolean getBoolean(@NotNull String key) {
        return this.backing.getBoolean(key);
    }

    @Override
    public @Nullable Integer getInt(@NotNull String key) {
        return this.backing.getInt(key);
    }

    @Override
    public @Nullable Long getLong(@NotNull String key) {
        return this.backing.getLong(key);
    }

    protected final <T> @NotNull T nullFallback(@Nullable T value, @NotNull T fallback) {
        if (value == null) return fallback;
        return value;
    }

}
