package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.impl.yaml.helpers.YamlLimits;
import io.github.wasabithumb.xclaim.config.helpers.ConfigComparators;
import io.github.wasabithumb.xclaim.config.sub.RulesConfig;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlRulesConfig extends YamlConfig implements RulesConfig {

    private final YamlLimits limits;
    public YamlRulesConfig(@Nullable ConfigurationSection section, @Nullable YamlLimits limits) {
        super(section);
        this.limits = limits;
    }

    private <T> @Nullable T placementTriple(@NotNull T none, @NotNull T cardinal, @NotNull T neighbor) {
        Boolean f1 = this.getBoolean("enforce-adjacent-claim-chunks");
        if (f1 == null) return null;
        if (!f1) return none;

        Boolean f2 = this.getBoolean("allow-diagonal-claim-chunks");
        if (f2 == null) return null;
        return f2 ? neighbor : cardinal;
    }

    @Override
    public @UnknownNullability Integer placementRaw() {
        return this.placementTriple(
                PlacementRule.NONE.code(),
                PlacementRule.CARDINAL.code(),
                PlacementRule.NEIGHBOR.code()
        );
    }

    @Override
    public @UnknownNullability PlacementRule placement() {
        return this.placementTriple(
                PlacementRule.NONE,
                PlacementRule.CARDINAL,
                PlacementRule.NEIGHBOR
        );
    }

    @Override
    public @UnknownNullability Integer minDistance() {
        return this.getInt("claim-min-distance");
    }

    @Override
    public @UnknownNullability Boolean exemptOwner() {
        return this.getBoolean("exempt-claim-owner-from-permission-rules");
    }

    @Override
    public @UnknownNullability Integer maxChunks(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "max-chunks", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Integer maxClaims(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "max-claims", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Integer maxClaimsInWorld(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "max-claims-in-world", ConfigComparators.INT_NATURAL_OR_INF);
    }

}
