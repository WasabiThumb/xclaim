package codes.wasabi.xclaim.platform.spigot_1_16;

import codes.wasabi.xclaim.platform.spigot_1_15.SpigotPlatform_1_15;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

public class SpigotPlatform_1_16 extends SpigotPlatform_1_15 {

    public SpigotPlatform_1_16() { }

    @Override
    public @Nullable ItemStack playerInventoryGetItem(PlayerInventory inv, EquipmentSlot slot) {
        return inv.getItem(slot);
    }

}
