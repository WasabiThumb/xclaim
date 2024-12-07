package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformInventory extends BukkitPlatformInventory {

    public static @NotNull BukkitPlatformInventory of(@NotNull PaperPlatform platform, @NotNull Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof PaperPlatformCustomInventory<?> custom) {
            return custom;
        }
        return new PaperPlatformInventory(platform, inventory);
    }

    //

    PaperPlatformInventory(@NotNull PaperPlatform platform, @NotNull Inventory handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

}
