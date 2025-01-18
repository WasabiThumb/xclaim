package io.github.wasabithumb.xclaim.platform.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public interface PlatformWorldManager extends Iterable<PlatformWorld> {

    @NotNull List<PlatformWorld> getAll();

    @Nullable PlatformWorld getWorld(@NotNull UUID uuid);

    @Nullable PlatformWorld getWorld(@NotNull String name);

    @Override
    default @NotNull Iterator<PlatformWorld> iterator() {
        return this.getAll().iterator();
    }

}
