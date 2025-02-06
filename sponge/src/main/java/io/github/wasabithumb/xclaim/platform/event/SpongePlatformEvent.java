package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.SpongePlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.registry.RegistryTypes;

public abstract class SpongePlatformEvent<E extends Event> implements PlatformEvent {

    protected final SpongePlatform platform;
    protected final E handle;

    protected SpongePlatformEvent(@NotNull SpongePlatform platform, @NotNull E handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull E handle() {
        return this.handle;
    }

    @Override
    public synchronized boolean isCancelled() {
        return (this.handle instanceof Cancellable c) && c.isCancelled();
    }

    @Override
    public synchronized void setCancelled(boolean cancelled) {
        if (this.handle instanceof Cancellable c)
            c.setCancelled(cancelled);
    }

    //

    protected <T> @NotNull T assertCause(@NotNull Class<T> clazz) {
        return this.handle.cause()
                .first(clazz)
                .orElseThrow(AssertionError::new);
    }

    protected @NotNull SpongePlatformPlayer assertPlayer() {
        return this.platform.adapter().player(this.assertCause(Player.class));
    }

    protected @NotNull PlatformEquipmentSlot inferHand() {
        return this.handle.context()
                .get(EventContextKeys.USED_HAND)
                .map(this::adaptHand)
                .orElse(PlatformEquipmentSlot.HAND);
    }

    private @NotNull PlatformEquipmentSlot adaptHand(@NotNull HandType ht) {
        if (RegistryUtil.referenceEquals(RegistryTypes.HAND_TYPE, ht, HandTypes.OFF_HAND))
            return PlatformEquipmentSlot.OFF_HAND;
        return PlatformEquipmentSlot.HAND;
    }

}
