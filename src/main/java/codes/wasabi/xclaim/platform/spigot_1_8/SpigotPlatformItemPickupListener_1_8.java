package codes.wasabi.xclaim.platform.spigot_1_8;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformItemPickupListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SpigotPlatformItemPickupListener_1_8 implements PlatformItemPickupListener, Listener {

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
    public void onPickup(PlayerPickupItemEvent event) {
        Player ply = event.getPlayer();
        Runnable cancel = () -> event.setCancelled(true);
        for (BiConsumer<Player, Runnable> consumer : callbacks) consumer.accept(ply, cancel);
    }

}
