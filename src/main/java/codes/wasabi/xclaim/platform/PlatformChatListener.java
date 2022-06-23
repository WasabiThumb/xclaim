package codes.wasabi.xclaim.platform;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public interface PlatformChatListener extends Listener {

    record PlatformChatListenerData(Player ply, String message, Runnable cancel) { }

    void onChat(Consumer<PlatformChatListenerData> cb);

    void unregister();

}
