package io.github.wasabithumb.xclaim.platform.data.material;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformMaterial {

    @Contract(pure = true)
    @NotNull String name();

    /** True if the material is "soil" (crops can be planted on it) */
    boolean isSoil();

    /** True if the material creates fire when interacted with */
    boolean ignites();

}
