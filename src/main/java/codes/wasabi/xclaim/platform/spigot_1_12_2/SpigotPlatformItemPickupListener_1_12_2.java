package codes.wasabi.xclaim.platform.spigot_1_12_2;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformItemPickupListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SpigotPlatformItemPickupListener_1_12_2 implements PlatformItemPickupListener, Listener {

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    private final List<BiConsumer<Player, Runnable>> callbacks = new ArrayList<>();
    @Override
    public void on(BiConsumer<Player, Runnable> callback) {
        callbacks.add(callback);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player ply = (Player) entity;
            Runnable cancel = () -> event.setCancelled(true);
            for (BiConsumer<Player, Runnable> consumer : callbacks) consumer.accept(ply, cancel);
        }
    }

}
