package io.github.wasabithumb.xclaim.trust;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface TrustManager extends AutoCloseable {

    @NotNull Collection<UUID> keys();

    @NotNull TrustSet get(@NotNull UUID target);

    @ApiStatus.Internal
    void trust(@NotNull UUID target, @NotNull UUID player);

    @ApiStatus.Internal
    void untrust(@NotNull UUID target, @NotNull UUID player);

}
