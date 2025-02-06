package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.math.vector.Vector3d;

import java.util.Queue;

public class SpongePlatformPlayerMoveEvent
        extends SpongePlatformEvent<MoveEntityEvent>
        implements PlatformPlayerMoveEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull MoveEntityEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!(handle.entity() instanceof Player)) return;
        boolean natural = handle.context()
                .get(EventContextKeys.MOVEMENT_TYPE)
                .map((MovementType mt) ->
                        RegistryUtil.referenceEquals(RegistryTypes.MOVEMENT_TYPE, mt, MovementTypes.NATURAL)
                )
                .orElse(true);
        if (!natural) return;
        queue.add(new SpongePlatformPlayerMoveEvent(platform, handle));
    }

    //

    private SpongePlatformPlayerMoveEvent(@NotNull SpongePlatform platform, @NotNull MoveEntityEvent handle) {
        super(platform, handle);
    }

    //

    private @NotNull PlatformLocation location(@NotNull Vector3d pos) {
        Entity ent = this.handle.entity();
        Vector3d rot = ent.rotation();
        return new PlatformLocation(
                this.platform.adapter().world(ent.world()),
                pos.x(), pos.y(), pos.z(),
                (float) rot.y(), (float) rot.x()
        );
    }

    //

    @Override
    public @UnknownNullability PlatformLocation getFrom() {
        return this.location(this.handle.originalPosition());
    }

    @Override
    public @UnknownNullability PlatformLocation getTo() {
        return this.location(this.handle.destinationPosition());
    }

    @Override
    public void setFrom(@NotNull PlatformLocation location) {
        // TODO
    }

    @Override
    public void setTo(@NotNull PlatformLocation location) {
        this.handle.setDestinationPosition(new Vector3d(location.x(), location.y(), location.z()));
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.entity());
    }

}
