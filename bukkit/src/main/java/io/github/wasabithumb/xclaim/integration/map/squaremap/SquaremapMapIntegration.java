package io.github.wasabithumb.xclaim.integration.map.squaremap;

import io.github.wasabithumb.xclaim.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.jpenilla.squaremap.api.BukkitAdapter;
import xyz.jpenilla.squaremap.api.Squaremap;
import xyz.jpenilla.squaremap.api.WorldIdentifier;

public final class SquaremapMapIntegration extends AbstractSquaremapMapIntegration {

    @Override
    protected @Nullable Squaremap load() {
        return Bukkit.getServicesManager().load(Squaremap.class);
    }

    @Override
    protected @Nullable WorldIdentifier getWorldIdentifier(@NotNull Claim claim) {
        World w = (World) this.adapter.world(claim.world());
        if (w == null) return null;
        return BukkitAdapter.worldIdentifier(w);
    }

}
