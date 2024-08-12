package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class XClaimRemoveChunksFromClaimEvent extends XClaimModifyClaimChunksEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    public XClaimRemoveChunksFromClaimEvent(@NotNull Player player, @NotNull Claim claim, @NotNull Chunk... chunks) {
        super(player, claim, chunks);
    }

    public XClaimRemoveChunksFromClaimEvent(@NotNull Player player, @NotNull Claim claim, @NotNull List<Chunk> chunks) {
        super(player, claim, chunks);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-remove-chunks";
    }

}
