package io.github.wasabithumb.xclaim.config.impl.toml;

import io.github.wasabithumb.jtoml.value.TomlValue;
import io.github.wasabithumb.jtoml.value.primitive.TomlPrimitive;
import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.Config;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

@ApiStatus.Internal
public class TomlConfig implements Config {

    protected final TomlTable table;
    protected final boolean valid;
    protected TomlConfig(@Nullable TomlTable table) {
        this.table = table;
        this.valid = table != null;
    }

    /**
     * This method will create a new TomlTable if the backing table is null. Hence, it's not really supposed
     * to be used when the backing table is null. The precondition "this.valid" should be checked first.
     */
    protected final @NotNull TomlTable raw() {
        if (this.valid) return Objects.requireNonNull(this.table);
        return TomlTable.create();
    }

    protected final @Nullable TomlValue getValue(@NotNull String key) {
        if (this.valid) {
            assert this.table != null;
            return this.table.get(key);
        }
        return null;
    }

    protected final @Nullable TomlTable getTable(@NotNull String key) {
        TomlValue tv = this.getValue(key);
        if (tv == null || !tv.isTable()) return null;
        return tv.asTable();
    }

    @Override
    public @Nullable Config sub(@NotNull String key) {
        return new TomlConfig(this.getTable(key));
    }

    @Override
    public final @Nullable String getString(final @NotNull String key) {
        return this.getPrimitive(key, TomlPrimitive::asString);
    }

    @Override
    public final @Nullable Boolean getBoolean(final @NotNull String key) {
        return this.getPrimitive(key, TomlPrimitive::asBoolean);
    }

    @Override
    public final @Nullable Integer getInt(final @NotNull String key) {
        return this.getPrimitive(key, TomlPrimitive::asInteger);
    }

    @Override
    public final @Nullable Long getLong(final @NotNull String key) {
        return this.getPrimitive(key, TomlPrimitive::asLong);
    }

    private <T> @Nullable T getPrimitive(@NotNull String key, @NotNull Function<TomlPrimitive, T> extractor) {
        TomlValue tv = this.getValue(key);
        if (tv == null || !tv.isPrimitive()) return null;
        return extractor.apply(tv.asPrimitive());
    }

}
