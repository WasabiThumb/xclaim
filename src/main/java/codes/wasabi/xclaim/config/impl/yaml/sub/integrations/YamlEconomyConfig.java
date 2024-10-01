package codes.wasabi.xclaim.config.impl.yaml.sub.integrations;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.impl.yaml.helpers.YamlLimits;
import codes.wasabi.xclaim.config.struct.helpers.ConfigComparators;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import it.unimi.dsi.fastutil.ints.IntComparators;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlEconomyConfig extends YamlConfig implements EconomyConfig {

    private final YamlLimits limits;
    public YamlEconomyConfig(@Nullable ConfigurationSection section, @Nullable YamlLimits limits) {
        super(section);
        this.limits = limits;
    }

    @Override
    public @UnknownNullability Integer claimPrice(@Nullable Permissible target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "claim-price", IntComparators.OPPOSITE_COMPARATOR);
    }

    @Override
    public @UnknownNullability Integer unclaimReward(@Nullable Permissible target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "unclaim-reward", IntComparators.NATURAL_COMPARATOR);
    }

    @Override
    public @UnknownNullability Integer freeChunks(@Nullable Permissible target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "free-chunks", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Boolean enabled() {
        return this.getBoolean("use-economy");
    }

}
