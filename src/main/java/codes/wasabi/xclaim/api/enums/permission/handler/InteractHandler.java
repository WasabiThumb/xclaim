package codes.wasabi.xclaim.api.enums.permission.handler;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

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

    private PlatformEntityPlaceListener entityPlaceListener = null;
    @Override
    protected void onRegister() {
        if (mode.equals(Mode.PLACE_ENTS) || mode.equals(Mode.PLACE_MOBILES)) {
            entityPlaceListener = Platform.get().getPlaceListener();
            if (entityPlaceListener != null) {
                entityPlaceListener.on(this::onPlaceEntity);
            }
        }
        // START Paper Item Frames
        if (!mode.equals(Mode.ALL)) return;
        Class<? extends Event> itemFrameEventClass;
        try {
            itemFrameEventClass = Class.forName("io.papermc.paper.event.player.PlayerItemFrameChangeEvent")
                    .asSubclass(Event.class);
        } catch (ClassNotFoundException | ClassCastException ignored) {
            return;
        }
        Bukkit.getPluginManager().registerEvent(
                itemFrameEventClass,
                this,
                EventPriority.NORMAL,
                this::onPaperItemFrameChange,
                XClaim.instance
        );
        // END Paper Item Frames
    }

    @Override
    protected void onUnregister() {
        if (entityPlaceListener != null) entityPlaceListener.unregister();
    }

    private <T extends PlayerEvent & Cancellable> boolean itemCheck(@NotNull T event) {
        Player ply = event.getPlayer();
        ItemStack is;
        Location loc = ply.getLocation();
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent pie = (PlayerInteractEvent) event;
            EquipmentSlot slot = Platform.get().getInteractHand(pie);
            if (slot == null) return false;
            is = Platform.get().playerInventoryGetItem(ply.getInventory(), slot);
            Block block = pie.getClickedBlock();
            if (block != null) {
                loc = Platform.get().toCenterLocation(block.getRelative(pie.getBlockFace()).getLocation());
            }
        } else {
            is = Platform.get().getPlayerItemInUse(ply);
            if (event instanceof PlayerInteractEntityEvent) {
                loc = ((PlayerInteractEntityEvent) event).getRightClicked().getLocation();
            }
        }
        if (is == null) return false;
        Material mat = is.getType();
        if (mode.equals(Mode.FLAMMABLE)) {
            if (!getClaim().contains(loc)) return false;
            boolean isFireCharge = false;
            Platform p = Platform.get();
            if (p.hasFireChargeMaterial()) {
                isFireCharge = mat.equals(p.getFireChargeMaterial());
            }
            if (mat.equals(Material.FLINT_AND_STEEL) || isFireCharge) {
                event.setCancelled(true);
                stdError(ply);
                return true;
            }
        } else {
            if (mat.equals(Material.WRITTEN_BOOK)) {
                if (getClaim().contains(loc)) {
                    Platform p = Platform.get();
                    if (p.supportsArtificialBookOpen()) {
                        event.setCancelled(true);
                        p.artificialBookOpen(ply, is);
                    } else {
                        if (ply.isSneaking()) {
                            event.setCancelled(true);
                            stdError(ply);
                        }
                    }
                }
                return true;
            } else if (mat.equals(Platform.get().getFireworkRocketMaterial())) {
                if (Platform.get().playerIsGliding(ply)) {
                    Platform p = Platform.get();
                    if (getClaim().contains(loc) && p.supportsArtificalElytraBoost()) {
                        event.setCancelled(true);
                        p.artificialElytraBoost(ply, is);

                        PlayerInventory inv = ply.getInventory();
                        ItemStack sample;
                        int i = 0;
                        final int size = inv.getSize();
                        for (; i < size; i++) {
                            sample = inv.getItem(i);
                            if (sample == null) continue;
                            if (sample.getType().equals(Platform.get().getFireworkRocketMaterial())) {
                                if (sample.getAmount() > 1) {
                                    sample.setAmount(sample.getAmount() - 1);
                                    inv.setItem(i, sample);
                                } else {
                                    inv.setItem(i, null);
                                }
                                break;
                            }
                        }
                        if (i == size) inv.remove(Platform.get().getFireworkRocketMaterial());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private final String[] legacyContainerClassNames = new String[] {
            "org.bukkit.block.Beacon",
            "org.bukkit.block.BrewingStand",
            "org.bukkit.block.Chest",
            "org.bukkit.block.Dispenser",
            "org.bukkit.block.Dropper",
            "org.bukkit.block.Furnace",
            "org.bukkit.block.Hopper",
            "org.bukkit.block.ShulkerBox"
    };
    private Class<?>[] legacyContainerClasses = null;
    private boolean legacyIsContainer(BlockState bs) {
        if (legacyContainerClasses == null) {
            Class<?>[] classes = new Class<?>[legacyContainerClassNames.length];
            int classCount = 0;
            for (String cn : legacyContainerClassNames) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(cn);
                } catch (Exception ignored) {
                    continue;
                }
                classes[classCount++] = clazz;
            }
            if (classCount < classes.length) {
                Class<?>[] cpy = new Class[classCount];
                System.arraycopy(classes, 0, cpy, 0, classCount);
                classes = cpy;
            }
            legacyContainerClasses = classes;
        }
        for (Class<?> c : legacyContainerClasses) {
            if (c.isInstance(bs)) return true;
        }
        return false;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (mode == Mode.CHESTS) {
            Block block = event.getClickedBlock();
            if (block != null) {
                BlockState bs = block.getState();
                boolean isContainer;
                if (PaperLib.isVersion(12, 2)) {
                    isContainer = (bs instanceof Container);
                } else {
                    isContainer = legacyIsContainer(bs);
                }
                if (isContainer) {
                    Player ply = event.getPlayer();
                    if (getClaim().hasPermission(ply, Permission.CHEST_OPEN)) return;
                    if (test(event, Platform.get().toCenterLocation(block.getLocation()))) {
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
        Location loc = Platform.get().getInteractionPoint(event);
        if (loc == null) {
            Block b = event.getClickedBlock();
            if (b != null) {
                loc = Platform.get().toCenterLocation(b.getLocation());
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
        if (ent instanceof Player) {
            Player ply = (Player) ent;
            if (getClaim().hasPermission(ply, Permission.CHEST_OPEN)) return;
            Inventory inv = event.getInventory();
            InventoryHolder holder = inv.getHolder();
            if (holder instanceof Container) {
                Block b;
                try {
                    b = ((Container) holder).getBlock();
                } catch (IllegalStateException ignored) {
                    return;
                }
                Location loc = Platform.get().toCenterLocation(b.getLocation());
                if (getClaim().contains(loc)) {
                    event.setCancelled(true);
                    stdError(ply);
                }
            }
        }
    }

    private void onPlaceEntity(@NotNull PlatformEntityPlaceListener.Data data) {
        if (!mode.equals(Mode.PLACE_ENTS) && !mode.equals(Mode.PLACE_MOBILES)) return;
        Player ply = data.player;
        if (ply == null) return;
        if (mode.equals(Mode.PLACE_ENTS)) {
            if (getClaim().hasPermission(ply, Permission.ENT_PLACE)) return;
        } else {
            if (getClaim().hasPermission(ply, Permission.VEHICLE_PLACE)) return;
            if (!(data.isVehicle)) return;
        }
        if (getClaim().contains(data.location)) {
            data.cancel.run();
            stdError(ply);
        }
    }

    // START Paper Item Frames
    protected void onPaperItemFrameChange(Listener ignored, Event e1) {
        Player ply = ((PlayerEvent) e1).getPlayer();
        Location loc;

        Class<?> cls = e1.getClass();
        try {
            Method getItemFrameMethod = cls.getMethod("getItemFrame");
            Entity itemFrame = (Entity) getItemFrameMethod.invoke(e1);
            loc = itemFrame.getLocation();
        } catch (ReflectiveOperationException | SecurityException ignored2) {
            loc = ply.getLocation();
        }

        if (test((Cancellable) e1, loc)) stdError(ply);
    }
    // END Paper Item Frames

    private boolean test(@NotNull Cancellable cancellable, @NotNull Location location) {
        if (getClaim().contains(location)) {
            cancellable.setCancelled(true);
            return true;
        }
        return false;
    }

}
