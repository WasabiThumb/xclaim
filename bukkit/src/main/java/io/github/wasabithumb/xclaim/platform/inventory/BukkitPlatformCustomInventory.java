package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitPlatformCustomInventory<D>
        extends BukkitPlatformInventory
        implements PlatformCustomInventory<D>, InventoryHolder
{

    protected final D data;
    BukkitPlatformCustomInventory(@NotNull BukkitPlatform platform, @NotNull D data) {
        super(platform);
        this.data = data;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.handle;
    }

    @Override
    public @NotNull D data() {
        return this.data;
    }

    @Override
    public @Nullable PlatformItem getItem(@NotNull PlatformEquipmentSlot slot) {
        return null;
    }

    @Override
    public void setItem(@NotNull PlatformEquipmentSlot slot, @Nullable PlatformItem item) {
    }

}
