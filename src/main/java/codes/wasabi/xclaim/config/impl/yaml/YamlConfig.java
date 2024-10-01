package codes.wasabi.xclaim.config.impl.yaml;

import codes.wasabi.xclaim.config.struct.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

@ApiStatus.Internal
public class YamlConfig implements Config {

    protected final ConfigurationSection section;
    protected final boolean valid;
    protected YamlConfig(@Nullable ConfigurationSection section) {
        this.section = section;
        this.valid = section != null;
    }

    /**
     * This method will create a new YamlConfiguration if the backing section is null. Hence, it's not really supposed
     * to be used when the backing section is null. The precondition "this.valid" should be checked first.
     */
    protected final @NotNull ConfigurationSection raw() {
        if (this.valid) return Objects.requireNonNull(this.section);
        return new YamlConfiguration();
    }

    protected final @Nullable ConfigurationSection getSection(@NotNull String key) {
        if (this.valid) return this.raw().getConfigurationSection(key);
        return null;
    }

    @Override
    public @Nullable Config sub(@NotNull String key) {
        return new YamlConfig(this.getSection(key));
    }

    @Override
    public final @Nullable String getString(final @NotNull String key) {
        return this.extract(key, (section) -> section.getString(key));
    }

    @Override
    public final @Nullable Boolean getBoolean(final @NotNull String key) {
        return this.extract(key, (section) -> section.getBoolean(key));
    }

    @Override
    public final @Nullable Integer getInt(final @NotNull String key) {
        return this.extract(key, (section) -> section.getInt(key));
    }

    @Override
    public final @Nullable Long getLong(final @NotNull String key) {
        return this.extract(key, (section) -> section.getLong(key));
    }

    private <T> @Nullable T extract(
            @NotNull String key,
            @NotNull Function<ConfigurationSection, T> extractor
    ) {
        if (!this.valid) return null;
        final ConfigurationSection raw = this.raw();
        if (!raw.contains(key)) return null;
        return extractor.apply(raw);
    }

}
