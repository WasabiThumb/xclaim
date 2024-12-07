package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitPlatformInventory implements PlatformInventory {

    protected final BukkitPlatform platform;
    protected Inventory handle;
    protected boolean handleSet;
    BukkitPlatformInventory(@NotNull BukkitPlatform platform, @NotNull Inventory handle) {
        this.platform = platform;
        this.handle = handle;
        this.handleSet = true;
    }

    BukkitPlatformInventory(@NotNull BukkitPlatform platform) {
        this.platform = platform;
        this.handle = null;
        this.handleSet = false;
    }

    @Override
    public @NotNull BukkitPlatform platform() {
        return this.platform;
    }

    @Override
    public @NotNull Inventory handle() {
        if (!this.handleSet)
            throw new IllegalStateException("Inventory handle not set");
        return this.handle;
    }

    protected void setHandle(@NotNull Inventory handle) {
        if (this.handleSet)
            throw new IllegalStateException("Inventory handle already set");
        this.handle = handle;
        this.handleSet = true;
    }

    @Override
    public int size() {
        return this.handle().getSize();
    }

    public @Nullable BukkitPlatformItem @NotNull [] getContents() {
        final ItemStack[] items = this.handle.getContents();
        final int len = items.length;
        final BukkitPlatformItem[] ret = new BukkitPlatformItem[len];
        for (int i=0; i < len; i++) ret[i] = this.platform.adapter().item(items[i]);
        return ret;
    }

    public void setContents(@Nullable PlatformItem @NotNull [] contents) {
        final int len = contents.length;
        final ItemStack[] items = new ItemStack[len];
        for (int i=0; i < len; i++) items[i] = this.platform.adapter().item(contents[i]);
        this.handle.setContents(items);
    }

    @Override
    public @Nullable BukkitPlatformItem getItem(int index) {
        return this.platform.adapter().item(this.handle.getItem(index));
    }

    @Override
    public @Nullable PlatformItem getItem(@NotNull PlatformEquipmentSlot slot) {
        if (this.handle instanceof PlayerInventory ply) {
            return this.platform.adapter().item(ply.getItem(this.platform.adapter().equipmentSlot(slot)));
        }
        return null;
    }

    @Override
    public void setItem(int index, @Nullable PlatformItem item) {
        this.handle().setItem(index, item == null ? null : ((BukkitPlatformItem) item).handle());
    }

    @Override
    public void setItem(@NotNull PlatformEquipmentSlot slot, @Nullable PlatformItem item) {
        if (this.handle instanceof PlayerInventory ply) {
            ply.setItem(
                    this.platform.adapter().equipmentSlot(slot),
                    this.platform.adapter().item(item)
            );
        }
    }

    @Override
    public @Nullable PlatformBlock getContainerBlock() {
        InventoryHolder holder = this.handle.getHolder();
        if (holder == null) return null;
        if (holder instanceof Container c) {
            return this.platform.adapter().block(c.getBlock());
        }
        return null;
    }

}
