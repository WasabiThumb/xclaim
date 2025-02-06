package io.github.wasabithumb.xclaim.platform.event.helper;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

import java.util.List;
import java.util.Optional;

public abstract class SpongePlatformInventoryEvent<E extends InteractContainerEvent>
        extends SpongePlatformEvent<E>
        implements PlatformInventoryEvent
{

    protected SpongePlatformInventoryEvent(
            @NotNull SpongePlatform platform,
            @NotNull E handle
    ) {
        super(platform, handle);
    }

    //

    protected @NotNull Container container() {
        return this.handle.container();
    }

    protected boolean affectsViewedInventory() {
        if (this.handle instanceof ClickContainerEvent c) {
            return c.slot()
                    .map(this.container()::isViewedSlot)
                    .orElse(true);
        }
        return true;
    }

    //

    @Override
    public @NotNull PlatformInventory inventory() {
        Optional<InventoryMenu> oim = this.container().currentMenu();
        if (oim.isPresent()) return this.platform.adapter().inventory(oim.get().inventory());

        List<Inventory> viewed = this.container().viewed();
        Inventory ret = null;
        for (Inventory next : viewed) {
            ret = next;
            if (!(ret instanceof PlayerInventory)) break;
        }

        if (ret == null) throw new AssertionError("Container has no viewed inventories");
        return this.platform.adapter().inventory(ret);
    }

}
