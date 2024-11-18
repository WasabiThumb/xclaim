package io.github.wasabithumb.xclaim.api.event;

import io.github.wasabithumb.xclaim.api.Claim;
import io.github.wasabithumb.xclaim.api.XCPlayer;
import io.github.wasabithumb.xclaim.api.enums.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XClaimRevokeUserPermissionEvent extends XClaimModifyClaimUserPermissionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    public XClaimRevokeUserPermissionEvent(
            @NotNull Player player,
            @NotNull Claim claim,
            @NotNull Permission permission,
            @NotNull XCPlayer target
    ) {
        super(player, claim, permission, target, false);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-revoke-user-permission";
    }

}
