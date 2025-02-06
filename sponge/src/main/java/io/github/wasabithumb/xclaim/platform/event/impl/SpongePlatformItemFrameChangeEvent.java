package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.AffectEntityEvent;

import java.util.Queue;

public class SpongePlatformItemFrameChangeEvent
        extends SpongePlatformEvent<AffectEntityEvent>
        implements PlatformItemFrameChangeEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull AffectEntityEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!handle.cause().containsType(Player.class)) return;
        for (Entity ent : handle.entities()) {
            if (!(ent instanceof Hanging hanging)) continue;
            queue.add(new SpongePlatformItemFrameChangeEvent(platform, handle, hanging));
        }
    }

    //

    private final Hanging entity;
    private SpongePlatformItemFrameChangeEvent(
            @NotNull SpongePlatform platform,
            @NotNull AffectEntityEvent handle,
            @NotNull Hanging entity
    ) {
        super(platform, handle);
        this.entity = entity;
    }

    //

    @Override
    public @NotNull PlatformEntity itemFrame() {
        return this.platform.adapter().entity(this.entity);
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
