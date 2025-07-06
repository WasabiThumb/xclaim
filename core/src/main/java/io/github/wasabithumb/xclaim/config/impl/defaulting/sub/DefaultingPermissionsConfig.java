package io.github.wasabithumb.xclaim.config.impl.defaulting.sub;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.config.impl.filter.sub.FilterPermissionsConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DefaultingPermissionsConfig extends FilterPermissionsConfig {

    public DefaultingPermissionsConfig(@NotNull PermissionsConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull TrustLevel defaultLevel(@NotNull Permission permission) {
        return Objects.requireNonNullElse(this.backing().defaultLevel(permission), TrustLevel.TRUSTED);
    }

    @Override
    public @NotNull TrustLevel wildLevel(@NotNull Permission permission) {
        return Objects.requireNonNullElse(this.backing().wildLevel(permission), TrustLevel.ALL);
    }

    @Override
    public @NotNull Boolean configurable(@NotNull Permission permission) {
        return Objects.requireNonNullElse(this.backing().configurable(permission), Boolean.TRUE);
    }

}
