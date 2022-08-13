package codes.wasabi.xclaim.platform.paper_1_17;

import codes.wasabi.xclaim.platform.spigot_1_17.SpigotPlatform_1_17;
import org.bukkit.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPlatform extends SpigotPlatform_1_17 {

    @Override
    public @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull String name) {
        return Bukkit.getOfflinePlayerIfCached(name);
    }

    @Override
    public void closeInventory(@NotNull Inventory iv) {
        iv.close();
    }

    @Override
    public long getLastSeen(@NotNull OfflinePlayer ply) {
        return ply.getLastSeen();
    }

    @Override
    public @NotNull Location toCenterLocation(@NotNull Location loc) {
        return loc.toCenterLocation();
    }

    @Override
    public @Nullable Location getInteractionPoint(@NotNull PlayerInteractEvent event) {
        return event.getInteractionPoint();
    }

}
