package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import org.jetbrains.annotations.NotNull;

public class XClaimPlugin extends AbstractXClaimPlugin {

    @Override
    protected @NotNull PaperPlatform createPlatform() {
        return new PaperPlatform(this);
    }

}
