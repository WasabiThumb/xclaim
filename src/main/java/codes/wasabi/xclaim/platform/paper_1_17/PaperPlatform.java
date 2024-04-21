package codes.wasabi.xclaim.platform.paper_1_17;

import codes.wasabi.xclaim.platform.PlatformChatListener;
import codes.wasabi.xclaim.platform.spigot_1_17.SpigotPlatform_1_17;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PaperPlatform extends SpigotPlatform_1_17 {

    @Override
    public void setOwningPlayer(SkullMeta sm, UUID owner, String username) {
        sm.setPlayerProfile(Bukkit.createProfile(owner, username));
    }

    @Override
    public void setOwningPlayer(SkullMeta sm, OfflinePlayer player) {
        sm.setPlayerProfile(player.getPlayerProfile());
    }

    @Override
    protected PlatformChatListener newChatListener() {
        return new PaperPlatformChatListener();
    }

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

    @Override
    public boolean supportsArtificalElytraBoost() {
        return true;
    }

    @Override
    public void artificialElytraBoost(Player ply, ItemStack is) {
        ply.boostElytra(is);
    }

}
