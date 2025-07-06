package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlPermissionsConfig extends YamlConfig implements PermissionsConfig {

    public YamlPermissionsConfig() {
        super(null);
    }

    @Override
    public @UnknownNullability TrustLevel defaultLevel(@NotNull Permission permission) {
        return null;
    }

    @Override
    public @UnknownNullability TrustLevel wildLevel(@NotNull Permission permission) {
        return null;
    }

    @Override
    public @UnknownNullability Boolean configurable(@NotNull Permission permission) {
        return null;
    }

}
