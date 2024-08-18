package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class XClaimModifyClaimUserPermissionEvent extends XClaimModifyClaimPermissionEvent {

    private final XCPlayer target;
    private final boolean value;

    public XClaimModifyClaimUserPermissionEvent(
            @NotNull Player player,
            @NotNull Claim claim,
            @NotNull Permission permission,
            @NotNull XCPlayer target,
            boolean value
    ) {
        super(player, claim, permission);
        this.target = target;
        this.value = value;
    }

    /**
     * Gets the player whose permissions are being modified.
     */
    public @NotNull XCPlayer getTarget() {
        return this.target;
    }

    /**
     * Returns true if the permission is being granted, false if the permission is being revoked.
     */
    public boolean getValue() {
        return this.value;
    }

}
