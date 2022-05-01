package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class DropHandler extends PermissionHandler {

    public DropHandler(@NotNull Claim claim) {
        super(claim);
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        Player ply = event.getPlayer();
        if (getClaim().hasPermission(ply, Permission.ITEM_DROP)) return;
        if (!getClaim().contains(ply.getLocation())) return;
        event.setCancelled(true);
        stdError(ply);
    }

}
