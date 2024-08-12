package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XClaimTransferOwnerEvent extends XClaimModifyClaimEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    private final XCPlayer previousOwner;
    private final XCPlayer newOwner;

    public XClaimTransferOwnerEvent(
            @NotNull Player player,
            @NotNull Claim claim,
            @NotNull XCPlayer previousOwner,
            @NotNull XCPlayer newOwner
    ) {
        super(player, claim);
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    /**
     * Gets the current owner of the claim
     */
    public @NotNull XCPlayer getPreviousOwner() {
        return this.previousOwner;
    }

    /**
     * Gets the target owner of the claim
     */
    public @NotNull XCPlayer getNewOwner() {
        return this.newOwner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-transfer-owner";
    }

}
