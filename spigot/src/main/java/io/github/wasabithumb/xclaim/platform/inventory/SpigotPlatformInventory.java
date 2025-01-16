package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformInventory extends BukkitPlatformInventory {

    public static @NotNull BukkitPlatformInventory of(@NotNull SpigotPlatform platform, @NotNull Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof SpigotPlatformCustomInventory<?> custom) {
            return custom;
        }
        return new SpigotPlatformInventory(platform, inventory);
    }

    //

    SpigotPlatformInventory(@NotNull SpigotPlatform platform, @NotNull Inventory handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) this.platform;
    }

}
