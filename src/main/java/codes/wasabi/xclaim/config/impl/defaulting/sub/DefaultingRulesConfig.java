package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterRulesConfig;
import codes.wasabi.xclaim.config.struct.sub.RulesConfig;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DefaultingRulesConfig extends FilterRulesConfig {

    public DefaultingRulesConfig(@NotNull RulesConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Integer placementRaw() {
        return this.nullFallback(this.backing().placementRaw(), PlacementRule.NEIGHBOR.code());
    }

    @Override
    public @NotNull PlacementRule placement() {
        return this.nullFallback(this.backing().placement(), PlacementRule.NEIGHBOR);
    }

    @Override
    public @NotNull Integer minDistance() {
        return this.nullFallback(this.backing().minDistance(), 0);
    }

    @Override
    public @NotNull Boolean exemptOwner() {
        return this.nullFallback(this.backing().exemptOwner(), true);
    }

    @Override
    public @NotNull Integer maxChunks(@Nullable Permissible target) {
        return this.nullFallback(this.backing().maxChunks(target), 20);
    }

    @Override
    public @NotNull Integer maxClaims(@Nullable Permissible target) {
        return this.nullFallback(this.backing().maxClaims(target), 5);
    }

    @Override
    public @NotNull Integer maxClaimsInWorld(@Nullable Permissible target) {
        return this.nullFallback(this.backing().maxClaimsInWorld(target), -1);
    }

}
