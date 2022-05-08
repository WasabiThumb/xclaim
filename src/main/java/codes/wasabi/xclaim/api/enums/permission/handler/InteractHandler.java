package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InteractHandler extends PermissionHandler {

    public enum Mode {
        ALL,
        CHESTS,
        PLACE_ENTS,
        PLACE_MOBILES,
        FLAMMABLE
    }

    public static class Chests extends InteractHandler {
        public Chests(@NotNull Claim claim) {
            super(claim, Mode.CHESTS);
        }
    }

    public static class Entities extends InteractHandler {
        public Entities(@NotNull Claim claim) {
            super(claim, Mode.PLACE_ENTS);
        }
    }

    public static class Vehicles extends InteractHandler {
        public Vehicles(@NotNull Claim claim) {
            super(claim, Mode.PLACE_MOBILES);
        }
    }

    public static class Flammable extends InteractHandler {
        public Flammable(@NotNull Claim claim) {
            super(claim, Mode.FLAMMABLE);
        }
    }

    private final Mode mode;
    public InteractHandler(@NotNull Claim claim, @NotNull Mode operationMode) {
        super(claim);
        mode = operationMode;
    }

    public InteractHandler(@NotNull Claim claim) {
        this(claim, Mode.ALL);
    }

    private <T extends PlayerEvent & Cancellable> boolean itemCheck(@NotNull T event) {
        Player ply = event.getPlayer();
        ItemStack is;
        Location loc = ply.getLocation();
        if (event instanceof PlayerInteractEvent pie) {
            EquipmentSlot slot = pie.getHand();
            if (slot == null) return false;
            is = ply.getInventory().getItem(slot);
            Block block = pie.getClickedBlock();
            if (block != null) {
                loc = block.getRelative(pie.getBlockFace()).getLocation().toCenterLocation();
            }
        } else {
            is = ply.getItemInUse();
            if (event instanceof PlayerInteractEntityEvent entityEvent) {
                loc = entityEvent.getRightClicked().getLocation();
            }
        }
        if (is == null) return false;
        Material mat = is.getType();
        if (mode.equals(Mode.FLAMMABLE)) {
            if (!getClaim().contains(loc)) return false;
            if (mat.equals(Material.FLINT_AND_STEEL) || mat.equals(Material.FIRE_CHARGE)) {
                event.setCancelled(true);
                return true;
            }
        } else {
            if (mat.equals(Material.WRITABLE_BOOK) || mat.equals(Material.WRITTEN_BOOK) || mat.equals(Material.KNOWLEDGE_BOOK)) return true;
        }
        return false;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (mode == Mode.CHESTS) {
            Block block = event.getClickedBlock();
            if (block != null) {
                BlockState bs = block.getState();
                if (bs instanceof Container) {
                    Player ply = event.getPlayer();
                    if (getClaim().hasPermission(ply, Permission.CHEST_OPEN)) return;
                    if (test(event, block.getLocation().toCenterLocation())) {
                        stdError(ply);
                    }
                }
            }
            return;
        }
        if (mode != Mode.ALL && mode != Mode.FLAMMABLE) return;
        Player ply = event.getPlayer();
        if (mode == Mode.FLAMMABLE) {
            if (getClaim().hasPermission(ply, Permission.FIRE_USE)) return;
        }
        if (getClaim().hasPermission(ply, Permission.INTERACT)) return;
        if (itemCheck(event)) return;
        Location loc = event.getInteractionPoint();
        if (loc == null) {
            Block b = event.getClickedBlock();
            if (b != null) {
                loc = b.getLocation().toCenterLocation();
            } else {
                loc = event.getPlayer().getLocation();
            }
        }
        if (test(event, loc)) {
            stdError(ply);
        }
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEntityEvent event) {
        if (mode != Mode.ALL && mode != Mode.FLAMMABLE) return;
        Player ply = event.getPlayer();
        if (mode == Mode.FLAMMABLE) {
            if (getClaim().hasPermission(ply, Permission.FIRE_USE)) return;
        }
        if (getClaim().hasPermission(ply, Permission.INTERACT)) return;
        if (itemCheck(event)) return;
        if (test(event, event.getRightClicked().getLocation())) {
            stdError(ply);
        }
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractAtEntityEvent event) {
        if (mode != Mode.ALL && mode != Mode.FLAMMABLE) return;
        Player ply = event.getPlayer();
        if (mode == Mode.FLAMMABLE) {
            if (getClaim().hasPermission(ply, Permission.FIRE_USE)) return;
        }
        if (getClaim().hasPermission(ply, Permission.INTERACT)) return;
        if (itemCheck(event)) return;
        if (test(event, event.getClickedPosition().toLocation(event.getRightClicked().getWorld()))) {
            stdError(ply);
        }
    }

    // deprecated?
    @EventHandler
    public void onOpenChest(@NotNull InventoryOpenEvent event) {
        if (!mode.equals(Mode.CHESTS)) return;
        HumanEntity ent = event.getPlayer();
        if (ent instanceof Player ply) {
            if (getClaim().hasPermission(ply, Permission.CHEST_OPEN)) return;
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();
            if (holder instanceof Container container) {
                Block b;
                try {
                    b = container.getBlock();
                } catch (IllegalStateException ignored) {
                    return;
                }
                Location loc = b.getLocation().toCenterLocation();
                if (getClaim().contains(loc)) {
                    event.setCancelled(true);
                    stdError(ply);
                }
            }
        }
    }

    @EventHandler
    public void onPlaceEntity(@NotNull EntityPlaceEvent event) {
        if (!mode.equals(Mode.PLACE_ENTS) && !mode.equals(Mode.PLACE_MOBILES)) return;
        Player ply = event.getPlayer();
        if (ply == null) return;
        Entity placed = event.getEntity();
        if (mode.equals(Mode.PLACE_ENTS)) {
            if (getClaim().hasPermission(ply, Permission.ENT_PLACE)) return;
        } else {
            if (getClaim().hasPermission(ply, Permission.VEHICLE_PLACE)) return;
            if (!(placed instanceof Vehicle)) return;
        }
        if (getClaim().hasPermission(ply, Permission.INTERACT)) return;
        if (getClaim().contains(placed.getLocation())) {
            event.setCancelled(true);
            stdError(ply);
        }
    }

    private boolean test(@NotNull Cancellable cancellable, @NotNull Location location) {
        if (getClaim().contains(location)) {
            cancellable.setCancelled(true);
            return true;
        }
        return false;
    }

}
