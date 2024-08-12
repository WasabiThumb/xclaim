package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a Claim has been initialized (all checks have passed), but not yet canonized.
 * Cancelling the event will stop the Claim from being effectively created.
 */
public class XClaimCreateClaimEvent extends XClaimModifyClaimEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    public XClaimCreateClaimEvent(@NotNull Player player, @NotNull Claim claim) {
        super(player, claim);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-create-claim";
    }

}
