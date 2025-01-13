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

    boolean canWaterlog();

    boolean isWaterlogged();

    default @NotNull PlatformBlock relative(int x, int y, int z) {
        return this.world().getBlock(
                this.x() + x,
                this.y() + y,
                this.z() + z
        );
    }

    default @NotNull PlatformBlock relative(@NotNull PlatformDirection direction) {
        return this.relative(direction.modX(), direction.modY(), direction.modZ());
    }

}
