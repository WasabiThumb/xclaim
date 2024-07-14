package codes.wasabi.xclaim.placeholder.impl;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CountInWorldPlaceholder implements Placeholder {

    private final Mode mode;
    public CountInWorldPlaceholder(Mode mode) {
        this.mode = mode;
    }

    public CountInWorldPlaceholder(boolean chunkMode) {
        this(chunkMode ? Mode.CHUNKS : Mode.CLAIMS);
    }

    //

    @Contract(pure = true)
    public @NotNull Mode getMode() {
        return this.mode;
    }

    @Override
    public @NotNull String getStem() {
        return this.mode.getStem();
    }

    @Override
    public boolean hasPositionalArgument() {
        return true;
    }

    @Override
    public @Nullable String computeFor(@NotNull OfflinePlayer player, @Nullable String arg) {
        if (arg == null) return null;

        World w = Bukkit.getWorld(arg);
        if (w == null) {
            for (World candidate : Bukkit.getWorlds()) {
                if (candidate.getName().equalsIgnoreCase(arg)) {
                    w = candidate;
                    break;
                }
            }
            if (w == null) return "0";
        }

        final UUID wid = w.getUID();

        int count = 0;
        for (Claim claim : Claim.getByOwner(player)) {
            w = claim.getWorld();
            if (w == null) continue;
            if (w.getUID().equals(wid)) count += this.mode.countFor(claim);
        }
        return Integer.toString(count);
    }

    //

    public enum Mode {
        CLAIMS,
        CHUNKS;

        String getStem() {
            switch (this) {
                case CHUNKS:
                    return "chunk_count_in";
                case CLAIMS:
                    return "claim_count_in";
            }
            throw new IllegalStateException();
        }

        int countFor(Claim claim) {
            if (this == CHUNKS) return claim.getChunks().size();
            return 1;
        }
    }

}
