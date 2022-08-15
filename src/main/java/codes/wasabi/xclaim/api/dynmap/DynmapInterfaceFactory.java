package codes.wasabi.xclaim.api.dynmap;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.dynmap.exception.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.logging.Level;

public final class DynmapInterfaceFactory {

    public static @NotNull DynmapInterface create() throws DynmapNotFoundException, DynmapNotEnabledException, DynmapInitializationException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("dynmap");
        if (plugin != null) {
            if (plugin.isEnabled()) {
                try {
                    if (plugin instanceof org.dynmap.bukkit.DynmapPlugin) {
                        org.dynmap.bukkit.DynmapPlugin dynmapPlugin = (org.dynmap.bukkit.DynmapPlugin) plugin;
                        boolean corePresent = false;
                        try {
                            Field f = org.dynmap.bukkit.DynmapPlugin.class.getDeclaredField("core");
                            try {
                                f.setAccessible(true);
                            } catch (Exception ignored) { }
                            corePresent = (f.get(dynmapPlugin) != null);
                        } catch (Exception e) {
                            XClaim.logger.log(Level.WARNING, XClaim.lang.get("dynmap-warn-core"));
                        }
                        if (!corePresent) {
                            throw new DynmapMissingCoreException("Dynmap plugin core is null");
                        }
                        return new codes.wasabi.xclaim.api.dynmap.DynmapInterface(dynmapPlugin);
                    } else {
                        throw new DynmapNotFoundException("Plugin named \"dynmap\" exists, but does not contain the known Dynmap API");
                    }
                } catch (Exception e) {
                    throw new DynmapInitializationException("Dynmap initialization failed unexpectedly", e);
                }
            } else {
                throw new DynmapNotEnabledException("Plugin is not enabled");
            }
        } else {
            throw new DynmapNotFoundException("Bukkit plugin manager cannot find plugin named \"dynmap\"");
        }
    }

    public static @Nullable DynmapInterface createElseNull() {
        try {
            return create();
        } catch (DynmapException e) {
            return null;
        }
    }

}
