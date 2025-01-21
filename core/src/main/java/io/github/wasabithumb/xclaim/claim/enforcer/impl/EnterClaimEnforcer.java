package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerMoveEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerTeleportEvent;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class EnterClaimEnforcer extends ClaimEnforcer {

    public EnterClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.ENTER;
    }

    //

    @PlatformEventHandler
    public void onMove(@NotNull PlatformPlayerMoveEvent event) {
        PlatformPlayer ply = event.player();
        PlatformLocation from = event.getFrom();
        PlatformLocation to = event.getTo();
        if (from == null || to == null) return;

        ChunkReference fromChunk = ChunkReference.of(from);
        ChunkReference toChunk = ChunkReference.of(to);
        if (fromChunk.matches(toChunk)) return;

        Claim toClaim = this.claimAt(toChunk);
        if (toClaim == null || toClaim.checkPermission(ply, Permission.ENTER)) return;

        Claim fromClaim = this.claimAt(fromChunk);
        if (toClaim.equals(fromClaim)) return;
        if (fromClaim != null && !fromClaim.checkPermission(ply, Permission.ENTER)) return;

        event.setCancelled(true);
        ply.sendMessage(this.lang().get(I18N.PERM_HANDLER_STD_ERROR));
    }

    @PlatformEventHandler
    public void onTeleport(@NotNull PlatformPlayerTeleportEvent event) {
        PlatformPlayer ply = event.player();
        Claim dest = this.claimAt(event.destination());

        if (dest == null) return;
        if (dest.checkPermission(ply, Permission.ENTER)) return;

        event.setCancelled(true);
        // TODO: More informative message
        ply.sendMessage(this.lang().get(I18N.PERM_HANDLER_STD_ERROR));
    }

}
