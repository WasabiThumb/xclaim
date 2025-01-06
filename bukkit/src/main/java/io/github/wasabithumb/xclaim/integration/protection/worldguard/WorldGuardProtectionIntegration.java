package io.github.wasabithumb.xclaim.integration.protection.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionIntegration;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionRegion;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.util.BABB;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldGuardProtectionIntegration implements ProtectionIntegration {

    private final WorldGuard api;

    private final WorldGuardPlugin plugin;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    public WorldGuardProtectionIntegration() throws IntegrationException {
        try {
            this.api = WorldGuard.getInstance();
            this.plugin = WorldGuardPlugin.inst();
        } catch (Exception e) {
            throw new IntegrationException("Failed to acquire WorldGuard instance", e);
        }
    }

    @Override
    public @NotNull Collection<ProtectionRegion> getRegionsAt(@NotNull ChunkReference chunk) {
        World w = (World) this.adapter.world(chunk.world);
        if (w == null) return Collections.emptyList();

        RegionManager mgr = this.api.getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(w));
        if (mgr == null) return Collections.emptyList();

        Map<String, ProtectedRegion> regions = mgr.getRegions();
        List<ProtectionRegion> ret = new ArrayList<>(1);

        BABB bounds = chunk.getBounds();
        for (ProtectedRegion region : regions.values()) {
            if (this.regionDoesNotIntersect(region, bounds)) continue;
            ret.add(new WorldGuardProtectionRegion(region, this.adapter, this.plugin));
        }

        return Collections.unmodifiableList(ret);
    }

    private boolean regionDoesNotIntersect(@NotNull ProtectedRegion region, @NotNull BABB bounds) {
        BlockVector3 mins = region.getMinimumPoint();
        BlockVector3 maxs = region.getMaximumPoint();
        BABB regionBounds = new BABB(
                mins.getX(), mins.getY(), mins.getZ(),
                maxs.getX(), maxs.getY(), maxs.getZ()
        );
        if (!bounds.overlaps(regionBounds)) return true;
        if (region instanceof ProtectedCuboidRegion) return false;

        // Expensive check! Most regions should be caught already, probably fine...
        for (int y=mins.getY(); y < maxs.getY(); y++) {
            for (int x=bounds.minX(); x < bounds.maxX(); x++) {
                for (int z=bounds.minZ(); z < bounds.maxZ(); z++) {
                    if (region.contains(x, y, z)) return false;
                }
            }
        }
        return true;
    }

}
