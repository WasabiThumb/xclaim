package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.BukkitPlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.event.helper.BukkitPlatformEntityEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitPlatformEntityDeathEvent extends BukkitPlatformEntityEvent<EntityDeathEvent> implements PlatformEntityDeathEvent {

    public BukkitPlatformEntityDeathEvent(@NotNull BukkitPlatform platform, @NotNull EntityDeathEvent handle) {
        super(platform, handle);
    }

    @Override
    public @NotNull List<PlatformItem> drops() {
        BukkitPlatformTypeAdapter adapter = this.platform.adapter();
        List<ItemStack> drops = this.handle.getDrops();
        return new ProxyList<>(
                drops,
                adapter::item,
                adapter::item
        );
    }

}
