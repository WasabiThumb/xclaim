package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.api.Claim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class XClaimModifyClaimChunksEvent extends XClaimModifyClaimEvent {

    protected final List<Chunk> chunks;

    public XClaimModifyClaimChunksEvent(@NotNull Player player, @NotNull Claim claim, @NotNull Chunk... chunks) {
        super(player, claim);
        this.chunks = Collections.unmodifiableList(Arrays.asList(chunks));
    }

    public XClaimModifyClaimChunksEvent(@NotNull Player player, @NotNull Claim claim, @NotNull List<Chunk> chunks) {
        super(player, claim);
        this.chunks = Collections.unmodifiableList(chunks);
    }

    //

    public @NotNull @Unmodifiable List<Chunk> getChunks() {
        return this.chunks;
    }

}
