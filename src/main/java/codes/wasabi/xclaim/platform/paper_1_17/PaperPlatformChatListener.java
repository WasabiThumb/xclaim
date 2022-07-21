package codes.wasabi.xclaim.platform.paper_1_17;

import codes.wasabi.xclaim.platform.PlatformChatListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaperPlatformChatListener implements PlatformChatListener {

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
    public void onMessage(@NotNull AsyncChatEvent event) {
        PlatformChatListenerData data = new PlatformChatListenerData(
                event.getPlayer(),
                PlainTextComponentSerializer.plainText().serialize(event.originalMessage()),
                () -> event.setCancelled(true)
        );
        for (Consumer<PlatformChatListenerData> consumer : callbacks) consumer.accept(data);
    }

}
