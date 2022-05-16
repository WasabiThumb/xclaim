package codes.wasabi.xclaim.api.dynmap;

import codes.wasabi.xclaim.api.dynmap.exception.DynmapException;
import codes.wasabi.xclaim.api.dynmap.exception.DynmapInitializationException;
import codes.wasabi.xclaim.api.dynmap.exception.DynmapNotEnabledException;
import codes.wasabi.xclaim.api.dynmap.exception.DynmapNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DynmapInterfaceFactory {

    public static @NotNull DynmapInterface create() throws DynmapNotFoundException, DynmapNotEnabledException, DynmapInitializationException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("dynmap");
        if (plugin != null) {
            if (plugin.isEnabled()) {
                try {
                    if (plugin instanceof org.dynmap.bukkit.DynmapPlugin dynmapPlugin) {
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
