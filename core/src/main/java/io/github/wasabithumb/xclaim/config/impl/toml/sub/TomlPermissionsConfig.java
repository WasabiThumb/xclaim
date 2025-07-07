package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.config.Config;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlPermissionsConfig extends TomlConfig implements PermissionsConfig {

    private final TomlConfig legacyDefaults;
    private final boolean hasLegacyDefaults;

    public TomlPermissionsConfig(@Nullable TomlTable table, @Nullable TomlTable legacyDefaults) {
        super(table);
        this.legacyDefaults = new TomlConfig(legacyDefaults);
        this.hasLegacyDefaults = legacyDefaults != null && !legacyDefaults.isEmpty();
    }

    @Override
    public @UnknownNullability TrustLevel defaultLevel(@NotNull Permission permission) {
        String value = this.withSub(permission.name(), (Config c) -> c.getString("default"));
        if (value == null && this.hasLegacyDefaults) {
            // Check the deprecated default-permissions block
            value = this.legacyDefaults.getString(permission.name());
            if (value == null) {
                for (String alias : permission.legacyNames()) {
                    value = this.legacyDefaults.getString(alias);
                    if (value != null) break;
                }
            }
        }
        return this.levelOf(value);
    }

    @Override
    public @UnknownNullability TrustLevel wildLevel(@NotNull Permission permission) {
        return this.levelOf(this.withSub(permission.name(), (Config c) -> c.getString("wild")));
    }

    @Override
    public @UnknownNullability Boolean configurable(@NotNull Permission permission) {
        return this.withSub(permission.name(), (Config c) -> c.getBoolean("configurable"));
    }

    @Contract("null -> null")
    private @Nullable TrustLevel levelOf(@Nullable String name) {
        if (name == null) return null;
        try {
            return TrustLevel.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

}
