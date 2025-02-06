package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.adapter.Adapter;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformDirection;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3i;

import java.util.Queue;

public class SpongePlatformPlayerInteractEvent
        extends SpongePlatformEvent<InteractBlockEvent>
        implements PlatformPlayerInteractEvent
{

    @Adapter
    public static void adapt(
            @NotNull SpongePlatform platform,
            @NotNull InteractBlockEvent handle,
            @NotNull Queue<PlatformEvent> queue
    ) {
        if (!handle.cause().containsType(Player.class)) return;
        queue.add(new SpongePlatformPlayerInteractEvent(platform, handle));
    }

    //

    private SpongePlatformPlayerInteractEvent(
            @NotNull SpongePlatform platform,
            @NotNull InteractBlockEvent handle
    ) {
        super(platform, handle);
    }

    //


    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public boolean isLeftClick() {
        return this.handle instanceof InteractBlockEvent.Primary;
    }

    @Override
    public boolean isRightClick() {
        return this.handle instanceof InteractBlockEvent.Secondary;
    }

    @Override
    public @NotNull PlatformEquipmentSlot getHand() {
        return this.inferHand();
    }

    @Override
    public @Nullable PlatformBlock getClickedBlock() {
        BlockSnapshot bs = this.handle.block();
        if (bs == null) return null;
        return SpongePlatformBlock.of(this.platform, bs);
    }

    @Override
    public @NotNull PlatformDirection face() {
        Direction d = this.handle.targetSide();
        Vector3i off = d.asBlockOffset();
        return PlatformDirection.of(off.x(), off.y(), off.z());
    }

    @Override
    public @NotNull PlatformPlayer player() {
        return this.assertPlayer();
    }

}
