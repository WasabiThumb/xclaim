package codes.wasabi.xclaim.gui;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.economy.Economy;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.InventorySerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.math.BigDecimal;
import java.util.*;

public class ChunkEditor {

    public static class Events implements Listener {

        private Events() { }

        @EventHandler
        public void onDrop(@NotNull PlayerDropItemEvent event) {
            Player ply = event.getPlayer();
            if (getEditing(ply) != null) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPickup(@NotNull EntityPickupItemEvent event) {
            Entity ent = event.getEntity();
            if (ent instanceof Player) {
                if (getEditing((Player) ent) != null) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onClick(@NotNull InventoryClickEvent event) {
            HumanEntity ent = event.getWhoClicked();
            if (ent instanceof Player) {
                Player ply = (Player) ent;
                if (getEditing(ply) != null) {
                    Inventory inv = event.getClickedInventory();
                    if (Objects.equals(inv, ply.getInventory())) event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onDrag(@NotNull InventoryDragEvent event) {
            HumanEntity ent = event.getWhoClicked();
            if (ent instanceof Player) {
                Player ply = (Player) ent;
                if (getEditing(ply) != null) {
                    Inventory inv = event.getInventory();
                    if (Objects.equals(inv, ply.getInventory())) event.setCancelled(true);
                }
            }
        }

        private boolean checkInventory(@NotNull Inventory inv) {
            if (inv instanceof PlayerInventory) {
                HumanEntity ent = ((PlayerInventory) inv).getHolder();
                if (ent instanceof Player) {
                    return getEditing((Player) ent) != null;
                }
            }
            return false;
        }

        @EventHandler
        public void onMove(@NotNull InventoryMoveItemEvent event) {
            Inventory a = event.getSource();
            Inventory b = event.getDestination();
            if (checkInventory(a)) {
                event.setCancelled(true);
                return;
            }
            if (checkInventory(b)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInteract(@NotNull PlayerInteractEvent event) {
            Action action = event.getAction();
            if (action == Action.PHYSICAL) return;
            Player ply = event.getPlayer();
            Claim claim = getEditing(ply);
            if (claim != null) {
                event.setCancelled(true);
                PlayerInventory inv = ply.getInventory();
                int slot = inv.getHeldItemSlot();
                switch (slot) {
                    case 1:
                        Chunk chunk = ply.getLocation().getChunk();
                        Claim existing = Claim.getByChunk(chunk);
                        if (existing != null) {
                            if (!existing.getOwner().getUniqueId().equals(ply.getUniqueId())) {
                                if (!(ply.hasPermission("xclaim.override") || ply.isOp())) {
                                    Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-taken"));
                                    break;
                                }
                            }
                        }
                        World w = claim.getWorld();
                        if (w != null) {
                            if (!w.getName().equalsIgnoreCase(chunk.getWorld().getName())) {
                                Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-wrong-world"));
                                break;
                            }
                        }
                        if (XClaim.mainConfig.getBoolean("enforce-adjacent-claim-chunks", true)) {
                            boolean diagonals = XClaim.mainConfig.getBoolean("allow-diagonal-claim-chunks", true);
                            boolean nextTo = false;
                            int targetX = chunk.getX();
                            int targetZ = chunk.getZ();
                            // gross
                            for (Chunk c : claim.getChunks()) {
                                int thisX = c.getX();
                                int thisZ = c.getZ();
                                int leftX = thisX - 1;
                                int rightX = thisX + 1;
                                boolean leftMatch = targetX == leftX;
                                boolean rightMatch = targetX == rightX;
                                if (targetZ == thisZ) {
                                    if (leftMatch) {
                                        nextTo = true;
                                        break;
                                    }
                                    if (rightMatch) {
                                        nextTo = true;
                                        break;
                                    }
                                }
                                int upZ = thisZ + 1;
                                int downZ = thisZ - 1;
                                boolean upMatch = targetZ == upZ;
                                boolean downMatch = targetZ == downZ;
                                if (targetX == thisX) {
                                    if (upMatch) {
                                        nextTo = true;
                                        break;
                                    }
                                    if (downMatch) {
                                        nextTo = true;
                                        break;
                                    }
                                }
                                if (diagonals) {
                                    if (upMatch) {
                                        if (leftMatch || rightMatch) {
                                            nextTo = true;
                                            break;
                                        }
                                    }
                                    if (downMatch) {
                                        if (leftMatch || rightMatch) {
                                            nextTo = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!nextTo) {
                                Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-adjacent"));
                                break;
                            }
                        }
                        XCPlayer xcp = XCPlayer.of(ply);
                        int numChunks = 0;
                        int maxChunks = xcp.getMaxChunks();
                        UUID uuid = ply.getUniqueId();
                        for (Claim c : Claim.getAll()) {
                            if (c.getOwner().getUniqueId().equals(uuid)) {
                                numChunks += c.getChunks().size();
                            }
                        }
                        if (numChunks >= maxChunks) {
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-max"));
                            break;
                        }
                        if (claim.addChunk(chunk)) {
                            if (Economy.isAvailable()) {
                                if (numChunks >= xcp.getFreeChunks()) {
                                    Economy eco = Economy.getAssert();
                                    double price = xcp.getClaimPrice();
                                    if (price > 0) {
                                        BigDecimal bd = BigDecimal.valueOf(price);
                                        if (!eco.canAfford(ply, bd)) {
                                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-cant-afford", eco.format(bd)));
                                            claim.removeChunk(chunk);
                                            break;
                                        }
                                        if (!eco.take(ply, bd)) {
                                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-pay-fail", eco.format(bd)));
                                            claim.removeChunk(chunk);
                                            break;
                                        }
                                        Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-pay-success", eco.format(bd)));
                                    }
                                }
                            }
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-add", chunk.getX(), chunk.getZ()));
                        } else {
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-redundant-add"));
                        }
                        break;
                    case 4:
                        Chunk chunk1 = ply.getLocation().getChunk();
                        if (claim.removeChunk(chunk1)) {
                            if (Economy.isAvailable()) {
                                Economy eco = Economy.getAssert();
                                XCPlayer xcp1 = XCPlayer.of(ply);
                                int numChunks1 = 0;
                                UUID uuid1 = ply.getUniqueId();
                                for (Claim c : Claim.getAll()) {
                                    if (c.getOwner().getUniqueId().equals(uuid1)) {
                                        numChunks1 += c.getChunks().size();
                                    }
                                }
                                if (numChunks1 >= xcp1.getFreeChunks()) {
                                    BigDecimal bd = BigDecimal.valueOf(xcp1.getUnclaimReward());
                                    eco.give(ply, bd);
                                    Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-reward", eco.format(bd)));
                                }
                            }
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-remove"));
                        } else {
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-redundant-remove"));
                        }
                        break;
                    case 7:
                        stopEditing(ply);
                        break;
                }
            }
        }

        @EventHandler
        public void onLeave(@NotNull PlayerQuitEvent event) {
            Player ply = event.getPlayer();
            if (XClaim.mainConfig.getBoolean("stop-editing-on-leave", true)) {
                stopEditing(ply);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onDeath(@NotNull PlayerDeathEvent event) {
            Player ply = event.getEntity();
            if (stopEditing(ply)) {
                boolean keepInventory = false;
                Boolean value = ply.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
                if (value == null) value = ply.getWorld().getGameRuleDefault(GameRule.KEEP_INVENTORY);
                if (value != null) keepInventory = value;
                if (!keepInventory) {
                    List<ItemStack> drops = event.getDrops();
                    drops.clear();
                    drops.addAll(Arrays.asList(ply.getInventory().getContents()));
                }
            }
        }

        @EventHandler
        public void onMove(@NotNull PlayerMoveEvent event) {
            Player ply = event.getPlayer();
            Claim editing;
            if ((editing = getEditing(ply)) != null) {
                Location from = event.getFrom();
                Location to = event.getTo();
                Chunk fromChunk = from.getChunk();
                Chunk toChunk = to.getChunk();
                if (toChunk.getX() != fromChunk.getX() || toChunk.getZ() != fromChunk.getZ()) {
                    int ownState = 0;
                    String langUnknown = XClaim.lang.get("unknown");
                    String ownerName = langUnknown;
                    if (editing.contains(to)) {
                        ownState = 1;
                    } else {
                        Claim cl = Claim.getByChunk(toChunk);
                        if (cl != null) {
                            XCPlayer xcp = cl.getOwner();
                            ownerName = Objects.requireNonNullElse(xcp.getName(), langUnknown);
                            ownState = (xcp.getUniqueId().equals(ply.getUniqueId()) ? 2 : 3);
                        }
                    }
                    Color color = Color.GRAY;
                    String refer = XClaim.lang.get("chunk-editor-info-open");
                    if (ownState == 1) {
                        color = Color.GREEN;
                        refer = XClaim.lang.get("chunk-editor-info-claimed");
                    } else if (ownState == 2) {
                        color = Color.YELLOW;
                        refer = XClaim.lang.get("chunk-editor-info-owned");
                    } else if (ownState == 3) {
                        color = Color.RED;
                        refer = XClaim.lang.get("chunk-editor-info-taken", ownerName);
                    }
                    TextColor tc = TextColor.color(color.asRGB());
                    Platform.getAdventure().player(ply).sendMessage(Component.empty()
                            .append(XClaim.lang.getComponent("chunk-editor-info", toChunk.getX(), toChunk.getZ()))
                            .append(Component.newline())
                            .append(Component.text(refer).color(tc))
                    );
                    ply.playSound(ply.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    java.awt.Color awtColor = new java.awt.Color(color.asRGB());
                    World w = toChunk.getWorld();
                    double eyeY = to.getY() + ply.getEyeHeight();
                    int targetY = Math.min(Math.max((int) Math.round(eyeY), Platform.get().getWorldMinHeight(w)), w.getMaxHeight() - 1);
                    for (int y = targetY - 2; y < targetY + 3; y++) {
                        Location origin = toChunk.getBlock(0, y, 0).getLocation();
                        for (double x = 0; x <= 16; x += 0.5d) {
                            Location aPos = origin.clone().add(x, 0, 0);
                            Location bPos = origin.clone().add(x, 0, 16);
                            (new ParticleBuilder(ParticleEffect.REDSTONE)).setColor(awtColor).setLocation(aPos).setAmount(1).setOffset(0.02f, 0.02f, 0.02f).display(ply);
                            (new ParticleBuilder(ParticleEffect.REDSTONE)).setColor(awtColor).setLocation(bPos).setAmount(1).setOffset(0.02f, 0.02f, 0.02f).display(ply);
                        }
                        for (double z = 0; z <= 16; z += 0.5d) {
                            Location aPos = origin.clone().add(0, 0, z);
                            Location bPos = origin.clone().add(16, 0, z);
                            (new ParticleBuilder(ParticleEffect.REDSTONE)).setColor(awtColor).setLocation(aPos).setAmount(1).setOffset(0.02f, 0.02f, 0.02f).display(ply);
                            (new ParticleBuilder(ParticleEffect.REDSTONE)).setColor(awtColor).setLocation(bPos).setAmount(1).setOffset(0.02f, 0.02f, 0.02f).display(ply);
                        }
                    }
                }
            }
        }

    }

    private static ItemStack CLAIM_STACK;
    private static ItemStack UNCLAIM_STACK;
    private static ItemStack QUIT_STACK;

    private static NamespacedKey KEY_FLAG;
    private static NamespacedKey KEY_NAME;
    private static NamespacedKey KEY_INVENTORY;
    private static Events EVENTS;
    private static boolean initialized = false;

    public static @NotNull NamespacedKey getNameKey() {
        return KEY_NAME;
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        CLAIM_STACK = DisplayItem.create(Material.GREEN_DYE, XClaim.lang.getComponent("chunk-editor-claim"));
        UNCLAIM_STACK = DisplayItem.create(Material.RED_DYE, XClaim.lang.getComponent("chunk-editor-unclaim"));
        QUIT_STACK = DisplayItem.create(Material.BARRIER, XClaim.lang.getComponent("chunk-editor-quit"));
        KEY_FLAG = Objects.requireNonNull(Platform.get().createNamespacedKey(XClaim.instance, "ce_flag"));
        KEY_NAME = Objects.requireNonNull(Platform.get().createNamespacedKey(XClaim.instance, "ce_name"));
        KEY_INVENTORY = Objects.requireNonNull(Platform.get().createNamespacedKey(XClaim.instance, "ce_inventory"));
        EVENTS = new Events();
        Bukkit.getPluginManager().registerEvents(EVENTS, XClaim.instance);
    }

    private static final Map<UUID, Claim> editingMap = new HashMap<>();
    public static @Nullable Claim getEditing(@NotNull Player ply) {
        UUID uuid = ply.getUniqueId();
        Claim ret = null;
        if (!editingMap.containsKey(uuid)) {
            PersistentDataContainer pdc = ply.getPersistentDataContainer();
            if (pdc.has(KEY_FLAG, PersistentDataType.BYTE)) {
                boolean flag = pdc.getOrDefault(KEY_FLAG, PersistentDataType.BYTE, (byte) 0) != ((byte) 0);
                if (flag) {
                    String name = pdc.get(KEY_NAME, PersistentDataType.STRING);
                    if (name != null) {
                        ret = Claim.getByName(name);
                        editingMap.put(uuid, ret);
                    }
                }
            }
        } else {
            ret = editingMap.get(uuid);
        }
        return ret;
    }

    public static boolean startEditing(@NotNull Player ply, @NotNull Claim claim) {
        if (getEditing(ply) != null) return false;
        UUID uuid = ply.getUniqueId();
        PersistentDataContainer pdc = ply.getPersistentDataContainer();
        pdc.set(KEY_NAME, PersistentDataType.STRING, claim.getName());
        pdc.set(KEY_INVENTORY, PersistentDataType.BYTE_ARRAY, InventorySerializer.serialize(ply.getInventory()));
        editingMap.put(uuid, claim);
        pdc.set(KEY_FLAG, PersistentDataType.BYTE, (byte) 1);
        PlayerInventory inv = ply.getInventory();
        inv.clear();
        inv.setItem(1, CLAIM_STACK);
        inv.setItem(4, UNCLAIM_STACK);
        inv.setItem(7, QUIT_STACK);
        return true;
    }

    public static boolean stopEditing(@NotNull Player ply) {
        if (getEditing(ply) == null) return false;
        UUID uuid = ply.getUniqueId();
        PersistentDataContainer pdc = ply.getPersistentDataContainer();
        pdc.set(KEY_FLAG, PersistentDataType.BYTE, (byte) 0);
        try {
            InventorySerializer.deserialize(pdc.getOrDefault(KEY_INVENTORY, PersistentDataType.BYTE_ARRAY, new byte[0]), ply.getInventory());
        } catch (IllegalArgumentException e) {
            ply.getInventory().clear();
        }
        editingMap.remove(uuid);
        return true;
    }

}
