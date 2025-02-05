package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformBlock;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.BlockEntityInventory;

public class SpongePlatformInventory implements PlatformInventory {

    protected final SpongePlatform platform;
    protected final Inventory handle;

    public SpongePlatformInventory(@NotNull SpongePlatform platform, @NotNull Inventory inventory) {
        this.platform = platform;
        this.handle = inventory;
    }

    //

    @Contract("null -> null")
    private PlatformItem adapt(ItemStack is) {
        if (is == null) return null;
        if (is.isEmpty()) return null;
        return this.platform.adapter().item(is);
    }

    private @NotNull EquipmentType adapt(@NotNull PlatformEquipmentSlot t) {
        return this.platform.adapter().equipmentSlot(t);
    }

    //

    @Override
    public @NotNull SpongePlatform platform() {
        return this.platform;
    }

    @Override
    public @NotNull Inventory handle() {
        return this.handle;
    }

    //

    public @Nullable Component title() {
        return null;
    }

    @Override
    public int size() {
        return this.handle.capacity();
    }

    @Override
    public @Nullable PlatformItem @NotNull [] getContents() {
        final int size = this.size();
        PlatformItem[] ret = new PlatformItem[size];
        for (int i=0; i < size; i++) {
            ret[i] = this.handle.peekAt(i)
                    .map(this::adapt)
                    .orElse(null);
        }
        return ret;
    }

    @Override
    public void setContents(@Nullable PlatformItem @NotNull [] contents) {
        PlatformItem item;
        for (int i=0; i < contents.length; i++) {
            item = contents[i];
            this.handle.set(
                    i,
                    item == null ? ItemStack.empty() : (ItemStack) item.handle()
            );
        }
    }

    @Override
    public @Nullable PlatformItem getItem(int index) {
        return this.handle.peekAt(index)
                .map(this::adapt)
                .orElse(null);
    }

    @Override
    public @Nullable PlatformItem getItem(@NotNull PlatformEquipmentSlot slot) {
        EquipmentInventory e;
        if (this.handle instanceof PlayerInventory pi) {
            e = pi.equipment();
        } else if (this.handle instanceof EquipmentInventory eq) {
            e = eq;
        } else {
            return null;
        }

        return e.peek(this.adapt(slot))
                .map(this::adapt)
                .orElse(null);
    }

    @Override
    public void setItem(int index, @Nullable PlatformItem item) {
        this.handle.set(
                index,
                item == null ? ItemStack.empty() : (ItemStack) item.handle()
        );
    }

    @Override
    public void setItem(@NotNull PlatformEquipmentSlot slot, @Nullable PlatformItem item) {
        EquipmentInventory e;
        if (this.handle instanceof PlayerInventory pi) {
            e = pi.equipment();
        } else if (this.handle instanceof EquipmentInventory eq) {
            e = eq;
        } else {
            return;
        }
        e.set(
                this.adapt(slot),
                item == null ? ItemStack.empty() : (ItemStack) item.handle()
        );
    }

    @Override
    public @Nullable PlatformBlock getContainerBlock() {
        if (this.handle instanceof BlockEntityInventory<?> bei) {
            return bei.blockEntity()
                    .map((BlockEntity be) -> SpongePlatformBlock.of(this.platform, be))
                    .orElse(null);
        }
        return null;
    }

    @Override
    public void clear() {
        this.handle.clear();
    }

}
