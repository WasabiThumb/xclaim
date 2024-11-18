package io.github.wasabithumb.xclaim.api.event;

import io.github.wasabithumb.xclaim.api.Claim;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class XClaimModifyClaimEvent extends XClaimEvent {

    private final Player player;
    private final Claim claim;
    public XClaimModifyClaimEvent(@NotNull Player player, @NotNull Claim claim) {
        this.player = player;
        this.claim = claim;
    }

    /**
     * Gets the player responsible for the modification; e.g. the player that clicked a GUI item or
     * ran a command that caused this event to be called.
     */
    @Override
    public final @NotNull Player getPlayer() {
        return this.player;
    }

    public final @NotNull Claim getClaim() {
        return this.claim;
    }

}
