package io.github.wasabithumb.xclaim.integration.protection;

import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ProtectionRegion {

    @NotNull Set<ProtectionPermission> getPermissions(@NotNull PlatformUser user);

}
