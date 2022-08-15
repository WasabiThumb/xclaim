package codes.wasabi.xclaim.platform;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public interface PlatformItemPickupListener {

    void register();

    void unregister();

    void on(BiConsumer<Player, Runnable> callback);

}
