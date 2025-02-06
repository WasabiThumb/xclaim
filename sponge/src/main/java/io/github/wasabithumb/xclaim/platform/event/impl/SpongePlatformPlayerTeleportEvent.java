package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.cause.entity.MovementType;
import org.spongepowered.api.event.cause.entity.MovementTypes;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.Queue;

public class SpongePlatformPlayerTeleportEvent
        extends SpongePlatformEvent<MoveEntityEvent>
        implements PlatformPlayerTeleportEvent
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
        if (natural) return;
        queue.add(new SpongePlatformPlayerTeleportEvent(platform, handle));
    }

    //

    private SpongePlatformPlayerTeleportEvent(@NotNull SpongePlatform platform, @NotNull MoveEntityEvent handle) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull PlatformPlayer player() {
        return this.platform.adapter().player(this.handle.entity());
    }

    @Override
    public @NotNull PlatformLocation destination() {
        ServerWorld destWorld;
        if (this.handle instanceof ChangeEntityWorldEvent e) {
            destWorld = e.destinationWorld();
        } else {
            destWorld = ((ServerPlayer) this.handle.entity()).world();
        }

        Vector3d pos = this.handle.destinationPosition();
        Vector3d rot = this.handle.entity().rotation();
        return new PlatformLocation(
                this.platform.adapter().world(destWorld),
                pos.x(), pos.y(), pos.z(),
                (float) rot.y(), (float) rot.x()
        );
    }

}
