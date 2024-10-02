package codes.wasabi.xclaim.gui;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui.page.MainPage;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformChatListener;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GUIHandler implements InventoryHolder, Listener {

    private static final Set<GUIHandler> openHandlers = new CopyOnWriteArraySet<>();

    public static void closeAll() {
        for (GUIHandler handler : openHandlers) {
            handler.close();
        }
    }

    private final Player target;
    private final Inventory inventory;
    private Page page = null;
    private boolean open = true;
    private PlatformSchedulerTask tick = null;
    private boolean shouldTick = false;
    private final PlatformChatListener chatListener;
    public GUIHandler(@NotNull Player target) {
        this.target = target;
        this.inventory = Platform.get().createInventory(this, 27, XClaim.lang.getComponent("gui-name"));
        switchPage(new MainPage(this));
        target.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
        chatListener = Platform.get().onChat();
        chatListener.onChat(this::onMessage);
        openHandlers.add(this);
    }

    public boolean getShouldTick() {
        return shouldTick;
    }

    public void setShouldTick(boolean _tick) {
        if (_tick == shouldTick) return;
        if (_tick) {
            tick = Platform.get().getScheduler().runTaskTimer(XClaim.instance, () -> {
                if (page != null) {
                    page.onTick();
                } else {
                    setShouldTick(false);
                }
            }, 0L, 1L);
            shouldTick = true;
        } else {
            if (tick != null) tick.cancel();
            shouldTick = false;
            tick = null;
        }
    }

    public @Nullable Page getActivePage() {
        return page;
    }

    public void switchPage(@Nullable Page page) {
        if (page == this.page) return;
        if (this.page != null) this.page.onExit();
        setShouldTick(false);
        this.page = page;
        if (page != null) {
            page.onEnter();
        } else {
            inventory.clear();
        }
        Player target = getTarget();
        target.playSound(target.getLocation(), Platform.get().getClickSound(), 1f, 1f);
    }

    public @NotNull Player getTarget() {
        return target;
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        if (!open) return;
        open = false;
        HandlerList.unregisterAll(this);
        Platform.get().closeInventory(inventory);
        chatListener.unregister();
        if (shouldTick) {
            shouldTick = false;
            if (tick != null) tick.cancel();
        }
        if (this.page != null) this.page.onExit();
        openHandlers.remove(this);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private boolean check(@NotNull InventoryEvent event) {
        Inventory inv = event.getInventory();
        InventoryHolder holder = inv.getHolder();
        return Objects.equals(holder, this);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (!check(event)) return;
        if (page != null) {
            if (page.suspended) return;
        }
        close();
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!check(event)) return;
        event.setCancelled(true);
        if (page != null) page.onClick(event.getSlot());
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (check(event)) event.setCancelled(true);
    }

    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        if (!open) return;
        Player ply = event.getPlayer();
        if (ply.getUniqueId().equals(target.getUniqueId())) {
            close();
        }
    }

    public void onMessage(@NotNull PlatformChatListener.Data data) {
        if (!open) return;
        Player ply = data.ply();
        if (ply.getUniqueId().equals(target.getUniqueId())) {
            if (page != null) {
                if (page.onMessage(data.message())) data.cancel().run();
            }
        }
    }

}
