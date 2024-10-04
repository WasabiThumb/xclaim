package codes.wasabi.xclaim.gui2;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.layout.GuiLayouts;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformChatListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuiManager implements GuiService, Listener {

    private final GuiLayouts layouts;
    private final Set<GuiInstance> instances = Collections.synchronizedSet(new HashSet<>());
    private final Set<UUID> chatTickets = Collections.synchronizedSet(new HashSet<>());
    private PlatformChatListener chatListener;
    public GuiManager() {
        this.layouts = new GuiLayouts();
    }

    @Contract(value = "-> !null", pure = true)
    public final GuiLayouts layouts() {
        return this.layouts;
    }

    @Override
    public void start() {
        this.layouts.startLoading();

        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
        this.chatListener = Platform.get().onChat();
        this.chatListener.onChat(this::onChat);
    }

    @Override
    public void stop() {
        this.clear();

        this.chatListener.unregister();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void openGui(@NotNull Player target) {
        final GuiInstance instance = GuiInstance.open(this, target, GuiSpecs.MAIN);
        this.instances.add(instance);
    }

    public void clear() {
        GuiInstance[] toClose;
        synchronized (this.instances) {
            final int len = this.instances.size();
            toClose = new GuiInstance[len];

            int i = 0;
            for (GuiInstance instance : this.instances) {
                toClose[i++] = instance;
            }

            this.instances.clear();
        }
        for (GuiInstance gui : toClose) gui.close();
        this.chatTickets.clear();
    }

    public void untrack(@NotNull GuiInstance instance) {
        this.instances.remove(instance);
    }

    void addChatTicket(@NotNull Player player) {
        this.chatTickets.add(player.getUniqueId());
    }

    // Listener Helpers

    private @Nullable GuiInstance getInstance(@NotNull Inventory inv) {
        final InventoryHolder holder = inv.getHolder();
        if (holder instanceof GuiInstance) return (GuiInstance) holder;
        return null;
    }

    private @Nullable GuiInstance getInstance(@NotNull InventoryEvent event) {
        return this.getInstance(event.getInventory());
    }

    // Listeners

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        GuiInstance instance = this.getInstance(event);
        if (instance == null) return;
        event.setCancelled(true);
        instance.click(event.getSlot());
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (this.getInstance(event) != null) event.setCancelled(true);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        GuiInstance instance = this.getInstance(event);
        if (instance == null) return;
        if (this.chatTickets.contains(instance.player().getUniqueId())) return;
        this.untrack(instance);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.instances.removeIf((GuiInstance i) -> i.player().getUniqueId().equals(uuid));
        this.chatTickets.remove(uuid);
    }

    public void onChat(@NotNull PlatformChatListener.Data data) {
        final Player ply = data.ply();
        final UUID uuid = ply.getUniqueId();
        if (!this.chatTickets.remove(uuid)) return;
        InventoryView view = ply.getOpenInventory();
        GuiInstance instance = this.getInstance(view.getTopInventory());
        if (instance == null) return;
        data.doCancel();
        instance.respond(data.message());
    }

}
