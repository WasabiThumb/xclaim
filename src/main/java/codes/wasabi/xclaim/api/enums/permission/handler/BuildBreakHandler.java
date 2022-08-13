package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        if (check(event, Platform.get().toCenterLocation(event.getBlock().getLocation()))) {
            stdError(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlace(@NotNull BlockMultiPlaceEvent event) {
        if (!place) return;
        if (getClaim().hasPermission(event.getPlayer(), Permission.BUILD)) return;
        for (BlockState bs : event.getReplacedBlockStates()) {
            if (check(event, Platform.get().toCenterLocation(bs.getLocation()))) {
                stdError(event.getPlayer());
                break;
            }
        }
    }

    // what a fucking mess
    private void onFluid(@NotNull PlayerInteractEvent event, boolean trueIfPlace) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player ply = event.getPlayer();
        if (getClaim().hasPermission(ply, Permission.BUILD)) return;
        ItemStack is = Platform.get().playerInventoryGetItem(ply.getInventory(), Objects.requireNonNullElse(event.getHand(), EquipmentSlot.HAND));
        if (is == null) return;
        if (is.getType().name().toUpperCase(Locale.ROOT).contains("BUCKET")) {
            boolean isBucket = is.getType().equals(Material.BUCKET);
            if (trueIfPlace == isBucket) return;
            Location loc = ply.getLocation();
            Block block = event.getClickedBlock();
            boolean hasWater = false;
            if (block != null) {
                BlockFace face = event.getBlockFace();
                BlockData bd = block.getBlockData();
                if (bd instanceof Waterlogged wl) {
                    hasWater = wl.isWaterlogged();
                    if (trueIfPlace && hasWater) {
                        Block relBlock = block.getRelative(face);
                        Material type = relBlock.getType();
                        hasWater = (type.equals(Material.WATER) || type.equals(Material.LEGACY_WATER) || type.equals(Material.LEGACY_STATIONARY_WATER) || type.equals(Material.LAVA) || type.equals(Material.LEGACY_LAVA) || type.equals(Material.LEGACY_STATIONARY_LAVA));
                        loc = Platform.get().toCenterLocation(relBlock.getLocation());
                    } else {
                        loc = Platform.get().toCenterLocation(block.getLocation());
                    }
                } else {
                    Block relBlock = block.getRelative(face);
                    Material type = relBlock.getType();
                    hasWater = (type.equals(Material.WATER) || type.equals(Material.LEGACY_WATER) || type.equals(Material.LEGACY_STATIONARY_WATER) || type.equals(Material.LAVA) || type.equals(Material.LEGACY_LAVA) || type.equals(Material.LEGACY_STATIONARY_LAVA));
                    loc = Platform.get().toCenterLocation(relBlock.getLocation());
                }
            }
            if (trueIfPlace == hasWater) return;
            if (check(event, loc)) stdError(ply);
        }
    }

    @EventHandler
    public void onPlaceFluid(@NotNull PlayerInteractEvent event) {
        if (!place) return;
        onFluid(event, true);
    }

    @EventHandler
    public void onRemoveFluid(@NotNull PlayerInteractEvent event) {
        if (!brk) return;
        onFluid(event, false);
    }

    @EventHandler
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (!brk) return;
        if (getClaim().hasPermission(event.getPlayer(), Permission.BREAK)) return;
        if (check(event, Platform.get().toCenterLocation(event.getBlock().getLocation()))) {
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
        if (check(event, Platform.get().toCenterLocation(event.getBlock().getLocation()))) {
            stdError(ply);
        }
    }

    @EventHandler
    public void onTrample(@NotNull PlayerInteractEvent event) {
        if (!brk) return;
        Action action = event.getAction();
        if (action.equals(Action.PHYSICAL)) {
            Block block = event.getClickedBlock();
            if (block != null) {
                Material type = block.getType();
                if (type.equals(Material.LEGACY_SOIL) || type.equals(Material.FARMLAND)) {
                    Player ply = event.getPlayer();
                    if (getClaim().hasPermission(ply, Permission.BREAK)) return;
                    if (check(event, Platform.get().toCenterLocation(block.getLocation()))) stdError(ply);
                }
            }
        }
    }

    @EventHandler
    public void onBreakEndCrystal(@NotNull EntityDamageByEntityEvent event) {
        if (!brk) return;
        Entity ent = event.getDamager();
        if (ent instanceof Player ply) {
            Entity victim = event.getEntity();
            if (victim instanceof EnderCrystal) {
                if (getClaim().hasPermission(ply, Permission.BREAK)) return;
                if (check(event, victim.getLocation())) stdError(ply);
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
