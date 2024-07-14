package codes.wasabi.xclaim.protection.impl.worldguard;

import codes.wasabi.xclaim.protection.ProtectionRegion;
import codes.wasabi.xclaim.protection.ProtectionService;
import codes.wasabi.xclaim.util.service.ServiceInitException;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WorldGuardProtectionService extends ProtectionService {

    private final com.sk89q.worldguard.WorldGuard worldGuard;
    private final com.sk89q.worldguard.bukkit.WorldGuardPlugin plugin;

    public WorldGuardProtectionService() throws ServiceInitException {
        try {
            Class<?> apiClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Method m = apiClass.getDeclaredMethod("getInstance");
            Object instance = m.invoke(null);
            this.worldGuard = (com.sk89q.worldguard.WorldGuard) instance;
        } catch (ReflectiveOperationException | SecurityException | LinkageError e) {
            throw new ServiceInitException("Failed to load WorldGuard API class", e);
        }
        try {
            Class<?> pluginClass = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            Method m = pluginClass.getDeclaredMethod("inst");
            Object instance = m.invoke(null);
            this.plugin = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) instance;
        } catch (ReflectiveOperationException | SecurityException | LinkageError e) {
            throw new ServiceInitException("Failed to load WorldGuard Plugin class", e);
        }
    }

    @Override
    public Collection<ProtectionRegion> getRegionsAt(Chunk chunk) {
        com.sk89q.worldguard.protection.managers.RegionManager manager = this.worldGuard.getPlatform().getRegionContainer().get(
                com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(chunk.getWorld())
        );
        if (manager == null) return Collections.emptyList();

        List<com.sk89q.worldedit.math.BlockVector2> vectors = new ArrayList<>();
        for (int x=0; x < 16; x++) {
            for (int z=0; z < 16; z++) {
                Block b = chunk.getBlock(x, 0, z);
                vectors.add(createWorldEditBlockVector2(
                        b.getX(),
                        b.getZ()
                ));
            }
        }

        List<ProtectionRegion> ret = new ArrayList<>();
        for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : manager.getRegions().values()) {
            if (!region.containsAny(vectors)) continue;
            ret.add(new codes.wasabi.xclaim.protection.impl.worldguard.WorldGuardProtectionRegion(
                    region,
                    this.plugin
            ));
        }

        return Collections.unmodifiableList(ret);
    }

    private static com.sk89q.worldedit.math.BlockVector2 createWorldEditBlockVector2(int x, int z) {
        try {
            Class<?> clazz = Class.forName("com.sk89q.worldedit.math.BlockVector2");
            Method m = clazz.getDeclaredMethod("at", Integer.TYPE, Integer.TYPE);
            Object o = m.invoke(null, x, z);
            return (com.sk89q.worldedit.math.BlockVector2) o;
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

}
