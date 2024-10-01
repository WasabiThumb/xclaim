package codes.wasabi.xclaim.config.impl.toml;

import codes.wasabi.xclaim.config.struct.Config;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

@ApiStatus.Internal
public class TomlConfig implements Config {

    protected final Toml table;
    protected final boolean valid;
    protected TomlConfig(@Nullable Toml table) {
        this.table = table;
        this.valid = table != null;
    }

    /**
     * This method will create a new Toml if the backing table is null. Hence, it's not really supposed
     * to be used when the backing table is null. The precondition "this.valid" should be checked first.
     */
    protected final @NotNull Toml raw() {
        if (this.valid) return Objects.requireNonNull(this.table);
        return new Toml();
    }

    protected final @Nullable Toml getTable(@NotNull String key) {
        if (this.valid) return this.raw().getTable(key);
        return null;
    }

    @Override
    public @Nullable Config sub(@NotNull String key) {
        return new TomlConfig(this.getTable(key));
    }

    @Override
    public final @Nullable String getString(final @NotNull String key) {
        return this.getPrimitive(key, Toml::getString);
    }

    @Override
    public final @Nullable Boolean getBoolean(final @NotNull String key) {
        return this.getPrimitive(key, Toml::getBoolean);
    }

    @Override
    public final @Nullable Integer getInt(final @NotNull String key) {
        Long value = this.getLong(key);
        if (value == null) return null;
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException ignored) {
            return null;
        }
    }

    @Override
    public final @Nullable Long getLong(final @NotNull String key) {
        return this.getPrimitive(key, Toml::getLong);
    }

    private <T> @Nullable T getPrimitive(@NotNull String key, @NotNull BiFunction<Toml, String, T> extractor) {
        if (this.valid) {
            try {
                return extractor.apply(this.raw(), key);
            } catch (ClassCastException ignored) { }
        }
        return null;
    }

}
