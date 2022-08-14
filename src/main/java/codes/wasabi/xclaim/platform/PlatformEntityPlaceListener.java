package codes.wasabi.xclaim.platform;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class PlatformEntityPlaceListener {

    public static class Data {
        public @Nullable Player player;
        public Runnable cancel;
        public boolean isVehicle;
        public Location location;
    }

    private final List<Consumer<Data>> callbacks = new ArrayList<>();

    public final void on(Consumer<Data> cb) {
        callbacks.add(cb);
    }

    public final void unregister() {
        onUnregister();
        callbacks.clear();
    }

    protected final void call(Data data) {
        for (Consumer<Data> con : callbacks) {
            con.accept(data);
        }
    }

    protected abstract void onUnregister();

}
