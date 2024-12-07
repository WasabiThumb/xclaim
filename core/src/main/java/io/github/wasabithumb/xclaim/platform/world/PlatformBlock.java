package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import org.jetbrains.annotations.NotNull;

public interface PlatformBlock extends PlatformObject {

    @NotNull PlatformWorld world();

    int x();

    int y();

    int z();

    default @NotNull PlatformLocation location() {
        return new PlatformLocation(this.world(), this.x(), this.y(), this.z());
    }

    @NotNull PlatformMaterial getType();

    void setType(@NotNull PlatformMaterial type);

    boolean isContainer();

}
