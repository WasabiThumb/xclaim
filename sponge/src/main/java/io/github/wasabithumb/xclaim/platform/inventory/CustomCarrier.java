package io.github.wasabithumb.xclaim.platform.inventory;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

@ApiStatus.Internal
final class CustomCarrier<D> implements Carrier {

    private final Component title;
    private final D data;
    private CarriedInventory<?> inventory;

    CustomCarrier(@NotNull Component title, @UnknownNullability D data) {
        this.title = title;
        this.data = data;
        this.inventory = null;
    }

    //

    public @NotNull Component title() {
        return this.title;
    }

    public @UnknownNullability D data() {
        return this.data;
    }

    @Override
    public synchronized CarriedInventory<?> inventory() {
        if (this.inventory == null)
            throw new IllegalStateException("Inventory has not been set");
        return this.inventory;
    }

    @Contract("null -> fail")
    public void setInventory(Inventory inventory) {
        if (inventory instanceof CarriedInventory<?> carried) {
            this.inventory = carried;
        } else {
            throw new IllegalArgumentException("Inventory is not a CarriedInventory");
        }
    }

}
