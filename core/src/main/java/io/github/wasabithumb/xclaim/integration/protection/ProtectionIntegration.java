package io.github.wasabithumb.xclaim.integration.protection;

import io.github.wasabithumb.xclaim.integration.Integration;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ProtectionIntegration extends Integration {

    @NotNull Collection<ProtectionRegion> getRegionsAt(@NotNull ChunkReference chunk);

}
