package codes.wasabi.xclaim.platform.spigot_1_13_2;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.spigot_1_13.SpigotPlatform_1_13;
import org.bukkit.Bukkit;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.jetbrains.annotations.Nullable;

public class SpigotPlatform_1_13_2 extends SpigotPlatform_1_13 {

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    private static class SpigotPlatformEntityPlaceListener_1_13_2 extends PlatformEntityPlaceListener implements Listener {
        public SpigotPlatformEntityPlaceListener_1_13_2() {
            Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
        }

        @EventHandler
        public void onPlace(EntityPlaceEvent event) {
            Data data = new Data();
            data.player = event.getPlayer();
            data.cancel = () -> event.setCancelled(true);
            data.isVehicle = (event.getEntity() instanceof Vehicle);
            data.location = (event.getEntity().getLocation());
            call(data);
        }

        @Override
        protected void onUnregister() {
            HandlerList.unregisterAll(this);
        }
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new SpigotPlatformEntityPlaceListener_1_13_2();
    }

}
