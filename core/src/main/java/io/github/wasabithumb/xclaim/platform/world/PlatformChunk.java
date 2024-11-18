package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import org.jetbrains.annotations.NotNull;

public interface PlatformChunk extends PlatformObject {

    @NotNull PlatformWorld world();

    int x();

    int z();

}
