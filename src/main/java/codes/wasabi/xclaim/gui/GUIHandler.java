package codes.wasabi.xclaim.gui;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui.page.MainPage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GUIHandler implements InventoryHolder, Listener {

    private final Player target;
    private final Inventory inventory;
    private Page page = null;
    private boolean open = true;
    private BukkitTask tick = null;
    private boolean shouldTick = false;
    public GUIHandler(@NotNull Player target) {
        this.target = target;
        this.inventory = Bukkit.createInventory(this, 27, Component.text("XClaim Config"));
        switchPage(new MainPage(this));
        target.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
    }

    public boolean getShouldTick() {
        return shouldTick;
    }

    public void setShouldTick(boolean _tick) {
        if (_tick == shouldTick) return;
        if (_tick) {
            tick = Bukkit.getScheduler().runTaskTimer(XClaim.instance, () -> {
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
        target.playSound(target.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
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
        inventory.close();
        HandlerList.unregisterAll(this);
        if (shouldTick) {
            shouldTick = false;
            if (tick != null) tick.cancel();
        }
        if (this.page != null) this.page.onExit();
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
        if (page != null) page.onClick(event.getSlot());
        event.setCancelled(true);
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

    @EventHandler
    public void onMessage(@NotNull AsyncChatEvent event) {
        if (!open) return;
        Player ply = event.getPlayer();
        if (ply.getUniqueId().equals(target.getUniqueId())) {
            if (page != null) {
                if (page.onMessage(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()))) event.setCancelled(true);
            }
        }
    }

}
