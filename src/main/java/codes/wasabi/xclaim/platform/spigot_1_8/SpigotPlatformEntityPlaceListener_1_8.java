package codes.wasabi.xclaim.platform.spigot_1_8;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class SpigotPlatformEntityPlaceListener_1_8 extends PlatformEntityPlaceListener implements Listener {
    public SpigotPlatformEntityPlaceListener_1_8() {
        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
    }

    protected EquipmentSlot getSlot(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) return null;
        return EquipmentSlot.HAND;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player ply = event.getPlayer();
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            EquipmentSlot es = getSlot(event);
            if (es == null) es = EquipmentSlot.HAND;
            ItemStack is = Platform.get().playerInventoryGetItem(ply.getInventory(), es);
            if (is == null) return;
            Material material = is.getType();
            String matName = material.name().toUpperCase(Locale.ROOT);
            Data data = new Data();
            data.cancel = () -> event.setCancelled(true);
            data.player = ply;
            Location interactPos;
            Block b = event.getClickedBlock();
            if (b != null) {
                BlockFace face = event.getBlockFace();
                interactPos = Platform.get().toCenterLocation(b.getRelative(face).getLocation());
            } else {
                interactPos = ply.getLocation();
            }
            data.location = interactPos;
            if (matName.contains("MINECART") || matName.contains("BOAT")) {
                data.isVehicle = true;
                call(data);
            } else if (matName.contains("ARMOR_STAND") || matName.contains("PAINTING") || matName.contains("ITEM_FRAME") || matName.contains("SPAWN_EGG")) {
                data.isVehicle = false;
                call(data);
            }
        }
    }

    @Override
    protected void onUnregister() {
        HandlerList.unregisterAll(this);
    }
}
