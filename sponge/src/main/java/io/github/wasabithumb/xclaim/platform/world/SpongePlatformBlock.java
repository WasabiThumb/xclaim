package io.github.wasabithumb.xclaim.platform.world;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.SpongePlatformMaterial;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.carrier.CarrierBlockEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.fluid.FluidTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

public class SpongePlatformBlock implements PlatformBlock {

    public static SpongePlatformBlock of(@NotNull SpongePlatform platform, @NotNull BlockEntity ent) {
        ServerLocation sl = ent.serverLocation();
        SpongePlatformBlock ret = new SpongePlatformBlock(
                platform,
                sl.world(),
                sl.blockX(),
                sl.blockY(),
                sl.blockZ()
        );
        ret.state = ent.block();
        return ret;
    }

    //

    private final SpongePlatform platform;
    private final ServerWorld world;
    private final int x;
    private final int y;
    private final int z;
    private transient BlockState state = null;

    public SpongePlatformBlock(
            @NotNull SpongePlatform platform,
            @NotNull ServerWorld world,
            int x,
            int y,
            int z
    ) {
        this.platform = platform;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //

    public synchronized @NotNull BlockState handle() {
        BlockState state = this.state;
        if (state == null) {
            state = this.world.block(this.x, this.y, this.z);
            this.state = state;
        }
        return state;
    }

    @Override
    public @NotNull PlatformWorld world() {
        return new SpongePlatformWorld(this.platform, this.world);
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Override
    public @NotNull PlatformMaterial getType() {
        return SpongePlatformMaterial.adaptBlock(this.handle().type());
    }

    @Override
    public void setType(@NotNull PlatformMaterial type) {
        this.world.setBlock(this.x, this.y, this.z, SpongePlatformMaterial.adaptBlock(type).defaultState());
    }

    @Override
    public boolean isContainer() {
        return this.world.blockEntity(this.x, this.y, this.z)
                .map((BlockEntity be) -> be instanceof CarrierBlockEntity)
                .orElse(Boolean.FALSE);
    }

    @Override
    public boolean canWaterlog() {
        return this.handle().supports(Keys.IS_WATERLOGGED);
    }

    @Override
    public boolean isWaterlogged() {
        return this.handle().fluidState().type() != FluidTypes.EMPTY;
    }

}
