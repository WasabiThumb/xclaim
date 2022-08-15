package codes.wasabi.xclaim.platform.spigot_1_9;

import codes.wasabi.xclaim.platform.spigot_1_8.SpigotPlatformEntityPlaceListener_1_8;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SpigotPlatformEntityPlaceListener_1_9 extends SpigotPlatformEntityPlaceListener_1_8 {

    public SpigotPlatformEntityPlaceListener_1_9() {
        super();
    }

    @Override
    protected EquipmentSlot getSlot(PlayerInteractEvent event) {
        return event.getHand();
    }

}
