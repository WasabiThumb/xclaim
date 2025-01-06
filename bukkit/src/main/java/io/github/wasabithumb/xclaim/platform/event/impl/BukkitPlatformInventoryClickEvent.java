package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformInventoryEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlatformInventoryClickEvent extends BukkitPlatformInventoryEvent<InventoryClickEvent> implements PlatformInventoryClickEvent {

    public BukkitPlatformInventoryClickEvent(@NotNull BukkitPlatform platform, @NotNull InventoryClickEvent handle) {
        super(platform, handle);
    }

    @Override
    public @Nullable PlatformPlayer player() {
        HumanEntity he = this.handle.getWhoClicked();
        if (he instanceof Player ply) {
            return this.platform().adapter().player(ply);
        }
        return null;
    }

    @Override
    public int slot() {
        return this.handle.getSlot();
    }

}
