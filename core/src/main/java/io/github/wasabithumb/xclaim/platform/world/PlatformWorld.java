package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlatformWorld extends PlatformObject {

    @NotNull UUID uuid();

    @NotNull String name();

    /** May cause the chunk to load! */
    @NotNull PlatformChunk getChunk(int x, int z);

    int getMinHeight();

    int getMaxHeight();

}
