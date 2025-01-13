package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.api.enums.TrustLevel;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.platform.entity.NamedPlatformEntityType;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformEntityExplodeEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public final class ExplodeClaimEnforcer extends ClaimEnforcer {

    public ExplodeClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.EXPLODE;
    }

    @PlatformEventHandler
    public void onExplode(@NotNull PlatformEntityExplodeEvent event) {
        List<PlatformBlock> blocks = event.blocks();
        int blockCount = blocks.size();
        if (blockCount == 0) return;

        PlatformEntity entity = event.entity();
        Map<Long, ClaimAndBlocks> ownedBlocks = new HashMap<>();
        int ownedBlockCount = 0;

        for (PlatformBlock block : blocks) {
            PlatformChunk chunk = block.location().chunk();
            long token = BitManipulation.i32i64(chunk.x(), chunk.z());
            ClaimAndBlocks cab = ownedBlocks.computeIfAbsent(
                    token,
                    (Long ignored) -> new ClaimAndBlocks(this.manager.getByChunk(chunk))
            );
            if (cab.claim == null) continue;
            cab.blocks.add(block);
            ownedBlockCount++;
        }

        if (ownedBlockCount == 0) return;

        int removedBlockCount = 0;
        for (ClaimAndBlocks entry : ownedBlocks.values()) {
            if (entry.claim == null) continue;
            if (!this.canExplode(entry.claim, entity)) {
                blocks.removeAll(entry.blocks);
                removedBlockCount += entry.blocks.size();
            }
        }

        if (blockCount == ownedBlockCount && ownedBlockCount == removedBlockCount) {
            event.setCancelled(true);
        }
    }

    private boolean canExplode(@NotNull Claim claim, @NotNull PlatformEntity entity) {
        TrustLevel tl = claim.getGlobalPermission(Permission.EXPLODE);
        if (tl.equals(TrustLevel.ALL)) return true;
        if (tl.equals(TrustLevel.NONE)) return false;

        if (entity.type().equals(NamedPlatformEntityType.CREEPER))
            return this.canCreeperExplode(claim, entity);

        if (entity.type().equals(NamedPlatformEntityType.TNT) || entity.type().equals(NamedPlatformEntityType.END_CRYSTAL))
            return this.canOtherExplode(claim, entity);

        return false;
    }

    private boolean canCreeperExplode(@NotNull Claim claim, @NotNull PlatformEntity entity) {
        PlatformEntity target = entity.target();
        if (target instanceof PlatformPlayer ply) {
            if (claim.checkPermission(ply, Permission.EXPLODE)) return true;
        }

        // Synthetic explosion for visual effect
        PlatformLocation loc = entity.location();
        PlatformWorld world = loc.world();
        world.createExplosion(loc, entity.isPoweredCreeper() ? 6 : 3, false, false);
        entity.remove();

        return false;
    }

    private boolean canOtherExplode(@NotNull Claim claim, @NotNull PlatformEntity entity) {
        PlatformPlayer source = entity.sourcePlayer();
        if (source == null) return false;
        if (claim.checkPermission(source, Permission.EXPLODE)) return true;
        source.sendMessage(this.lang().get("permHandler-stdError"));
        return false;
    }

    //

    private static final class ClaimAndBlocks {

        final Claim claim;
        final List<PlatformBlock> blocks;

        ClaimAndBlocks(@Nullable Claim claim) {
            this.claim = claim;
            this.blocks = new LinkedList<>();
        }

    }

}
