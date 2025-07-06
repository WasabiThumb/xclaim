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

    public TomlPermissionsConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability TrustLevel defaultLevel(@NotNull Permission permission) {
        return this.levelOf(this.withSub(permission.name(), (Config c) -> c.getString("default")));
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
