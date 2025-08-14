package io.github.wasabithumb.xclaim.integration.map.squaremap;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerWorld;
import xyz.jpenilla.squaremap.api.Squaremap;
import xyz.jpenilla.squaremap.api.SquaremapProvider;
import xyz.jpenilla.squaremap.api.WorldIdentifier;

public final class SquaremapMapIntegration extends AbstractSquaremapMapIntegration {

    @Override
    protected @NotNull Squaremap load() {
        try {
            return SquaremapProvider.get();
        } catch (IllegalStateException e) {
            throw new IntegrationException("Failed to acquire Squaremap instance", e);
        }
    }

    @Override
    protected @Nullable WorldIdentifier getWorldIdentifier(@NotNull Claim claim) {
        ServerWorld world = (ServerWorld) this.adapter.world(claim.world());
        if (world == null) return null;
        ResourceKey key = world.key();
        return WorldIdentifier.create(
                key.namespace(),
                key.value()
        );
    }

}
