package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.SpongePlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.event.helper.SpongePlatformInventoryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Optional;
import java.util.Queue;

public class SpongePlatformInventoryClickEvent
        extends SpongePlatformInventoryEvent<ClickContainerEvent>
        implements PlatformInventoryClickEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull ClickContainerEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        SpongePlatformInventoryClickEvent ret = new SpongePlatformInventoryClickEvent(platform, handle);
        if (ret.optionalSlot().isEmpty()) return;
        if (!ret.affectsViewedInventory()) return;
        queue.add(ret);
    }

    //

    private SpongePlatformInventoryClickEvent(
            @NotNull SpongePlatform platform,
            @NotNull ClickContainerEvent handle
    ) {
        super(platform, handle);
    }

    //

    private Optional<Integer> optionalSlot() {
        return this.handle.slot().flatMap((Slot s) -> s.get(Keys.SLOT_INDEX));
    }

    @Override
    public int slot() {
        return this.optionalSlot().orElseThrow(AssertionError::new);
    }

    @Override
    public @Nullable PlatformPlayer player() {
        SpongePlatformTypeAdapter adapter = this.platform.adapter();
        return this.handle.cause()
                .first(Player.class)
                .map(adapter::player)
                .orElse(null);
    }

}
