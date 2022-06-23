package codes.wasabi.xclaim.platform.spigot;

import codes.wasabi.xclaim.platform.PlatformChatListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpigotPlatformChatListener implements PlatformChatListener {

    private final List<Consumer<PlatformChatListenerData>> callbacks = new ArrayList<>();

    @Override
    public void onChat(Consumer<PlatformChatListenerData> cb) {
        callbacks.add(cb);
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
        callbacks.clear();
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        PlatformChatListenerData data = new PlatformChatListenerData(
                event.getPlayer(),
                event.getMessage(),
                () -> event.setCancelled(true)
        );
        for (Consumer<PlatformChatListenerData> consumer : callbacks) consumer.accept(data);
    }

}
