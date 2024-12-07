package io.github.wasabithumb.xclaim.trust;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface TrustSet extends Set<UUID> {

    @NotNull UUID owner();

    @Contract("null -> false")
    @Override
    boolean contains(Object o);

    @Contract("null -> fail")
    @Override
    boolean add(UUID uuid);

    @Contract("null -> false")
    @Override
    boolean remove(Object o);

}
