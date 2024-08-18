package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XClaimSetPermissionEvent extends XClaimModifyClaimPermissionEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    private final TrustLevel previousLevel;
    private final TrustLevel newLevel;

    public XClaimSetPermissionEvent(
            @NotNull Player player,
            @NotNull Claim claim,
            @NotNull Permission permission,
            @NotNull TrustLevel previousLevel,
            @NotNull TrustLevel newLevel
    ) {
        super(player, claim, permission);
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
    }

    public final @NotNull TrustLevel getPreviousLevel() {
        return this.previousLevel;
    }

    public final @NotNull TrustLevel getNewLevel() {
        return this.newLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-set-permission";
    }

}
