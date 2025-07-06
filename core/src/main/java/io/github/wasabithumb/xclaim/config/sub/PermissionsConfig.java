package io.github.wasabithumb.xclaim.config.sub;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface PermissionsConfig extends Config {

    @UnknownNullability TrustLevel defaultLevel(@NotNull Permission permission);

    @UnknownNullability TrustLevel wildLevel(@NotNull Permission permission);

    @UnknownNullability Boolean configurable(@NotNull Permission permission);

}
