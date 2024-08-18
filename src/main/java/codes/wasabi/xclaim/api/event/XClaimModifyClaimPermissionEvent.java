package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class XClaimModifyClaimPermissionEvent extends XClaimModifyClaimEvent {

    private final Permission permission;
    public XClaimModifyClaimPermissionEvent(@NotNull Player player, @NotNull Claim claim, @NotNull Permission permission) {
        super(player, claim);
        this.permission = permission;
    }

    /**
     * Gets the permission being modified
     */
    public final @NotNull Permission getPermission() {
        return this.permission;
    }

}
