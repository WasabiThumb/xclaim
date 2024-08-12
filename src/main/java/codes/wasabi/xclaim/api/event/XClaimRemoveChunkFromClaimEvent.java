package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class XClaimRemoveChunkFromClaimEvent extends XClaimRemoveChunksFromClaimEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    //

    public XClaimRemoveChunkFromClaimEvent(@NotNull Player player, @NotNull Claim claim, @NotNull Chunk chunk) {
        super(player, claim, Collections.singletonList(chunk));
    }

    public @NotNull Chunk getChunk() {
        return this.chunks.get(0);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    protected @NotNull String getTranslatableFailMessage() {
        return "event-fail-remove-chunk";
    }
}
