package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public class BuildBreakHandler extends PermissionHandler {

    public static final class Build extends BuildBreakHandler {
        public Build(@NotNull Claim claim) {
            super(claim, true, false);
        }
    }

    public static final class Break extends BuildBreakHandler {
        public Break(@NotNull Claim claim) {
            super(claim, false, true);
        }
    }

    private final boolean place;
    private final boolean brk;

    public BuildBreakHandler(@NotNull Claim claim, boolean activateOnPlace, boolean activateOnBreak) {
        super(claim);
        place = activateOnPlace;
        brk = activateOnBreak;
    }

    @EventHandler
    public void onPlace(@NotNull BlockPlaceEvent event) {
        if (!place) return;
        if (getClaim().hasPermission(event.getPlayer(), Permission.BUILD)) return;
        if (check(event, event.getBlock().getLocation().toCenterLocation())) {
            stdError(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlace(@NotNull BlockMultiPlaceEvent event) {
        if (!place) return;
        if (getClaim().hasPermission(event.getPlayer(), Permission.BUILD)) return;
        for (BlockState bs : event.getReplacedBlockStates()) {
            if (check(event, bs.getLocation().toCenterLocation())) {
                stdError(event.getPlayer());
                break;
            }
        }
    }

    @EventHandler
    public void onPlaceFluid(@NotNull PlayerInteractEvent event) {
        if (!place) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player ply = event.getPlayer();
        if (getClaim().hasPermission(ply, Permission.BUILD)) return;
        ItemStack is = ply.getInventory().getItem(Objects.requireNonNullElse(event.getHand(), EquipmentSlot.HAND));
        if (is == null) return;
        if (is.getType().name().toUpperCase(Locale.ROOT).contains("BUCKET")) {
            event.setCancelled(true);
            stdError(ply);
        }
    }

    @EventHandler
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (!brk) return;
        if (getClaim().hasPermission(event.getPlayer(), Permission.BREAK)) return;
        if (check(event, event.getBlock().getLocation().toCenterLocation())) {
            stdError(event.getPlayer());
        }
    }

    @EventHandler
    public void onBreak(@NotNull HangingBreakByEntityEvent event) {
        if (!brk) return;
        if (!getClaim().contains(event.getEntity().getLocation())) return;
        Entity remover = event.getRemover();
        if (remover == null) return;
        if (remover instanceof Player ply) {
            if (getClaim().hasPermission(ply, Permission.BREAK)) return;
            event.setCancelled(true);
            stdError(ply);
        }
    }

    @EventHandler
    public void onDamage(@NotNull BlockDamageEvent event) {
        if (!brk) return;
        Player ply = event.getPlayer();
        if (getClaim().hasPermission(ply, Permission.BREAK)) return;
        if (check(event, event.getBlock().getLocation().toCenterLocation())) {
            stdError(ply);
        }
    }

    @EventHandler
    public void onTrample(@NotNull PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action.equals(Action.PHYSICAL)) {
            Block block = event.getClickedBlock();
            if (block != null) {
                Material type = block.getType();
                if (type.equals(Material.LEGACY_SOIL) || type.equals(Material.FARMLAND)) {
                    Player ply = event.getPlayer();
                    if (getClaim().hasPermission(ply, Permission.BREAK)) return;
                    if (check(event, block.getLocation().toCenterLocation())) stdError(ply);
                }
            }
        }
    }

    private boolean check(@NotNull Cancellable cancellable, @NotNull Location location) {
        if (getClaim().contains(location)) {
            cancellable.setCancelled(true);
            return true;
        }
        return false;
    }

}
