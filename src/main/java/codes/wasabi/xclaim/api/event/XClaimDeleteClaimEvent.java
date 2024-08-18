package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a Claim is deleted by a player.
 * Cancelling the event will prevent the Claim from being deleted.
 */
public class XClaimDeleteClaimEvent extends XClaimModifyClaimEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    public XClaimDeleteClaimEvent(@NotNull Player player, @NotNull Claim claim) {
        super(player, claim);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-delete-claim";
    }

}
