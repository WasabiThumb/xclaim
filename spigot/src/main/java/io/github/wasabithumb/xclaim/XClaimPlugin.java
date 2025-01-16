package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import org.jetbrains.annotations.NotNull;

public class XClaimPlugin extends AbstractXClaimPlugin {

    @Override
    protected @NotNull SpigotPlatform createPlatform() {
        return new SpigotPlatform(this);
    }

}
