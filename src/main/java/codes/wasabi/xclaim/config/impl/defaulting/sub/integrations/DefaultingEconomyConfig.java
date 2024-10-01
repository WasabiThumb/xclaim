package codes.wasabi.xclaim.config.impl.defaulting.sub.integrations;

import codes.wasabi.xclaim.config.impl.filter.sub.integrations.FilterEconomyConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DefaultingEconomyConfig extends FilterEconomyConfig {

    public DefaultingEconomyConfig(@NotNull EconomyConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Boolean enabled() {
        return this.nullFallback(this.backing().enabled(), false);
    }

    @Override
    public @NotNull Boolean debug() {
        return this.nullFallback(this.backing().debug(), false);
    }

    @Override
    public @NotNull Integer claimPrice(@Nullable Permissible target) {
        return this.nullFallback(this.backing().claimPrice(target), 20);
    }

    @Override
    public @NotNull Integer unclaimReward(@Nullable Permissible target) {
        return this.nullFallback(this.backing().unclaimReward(target), 0);
    }

    @Override
    public @NotNull Integer freeChunks(@Nullable Permissible target) {
        return this.nullFallback(this.backing().freeChunks(target), 4);
    }

}
