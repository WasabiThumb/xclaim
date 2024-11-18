package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.platform.Platform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface XClaimBootstrap {

    @NotNull Platform platform();

}
