package io.github.wasabithumb.xclaim.config.impl.yaml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.impl.yaml.helpers.YamlLimits;
import io.github.wasabithumb.xclaim.config.helpers.ConfigComparators;
import io.github.wasabithumb.xclaim.config.sub.integrations.EconomyConfig;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Comparator;

public final class YamlEconomyConfig extends YamlConfig implements EconomyConfig {

    private final YamlLimits limits;
    public YamlEconomyConfig(@Nullable ConfigurationSection section, @Nullable YamlLimits limits) {
        super(section);
        this.limits = limits;
    }

    @Override
    public @UnknownNullability Integer claimPrice(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "claim-price", Comparator.reverseOrder());
    }

    @Override
    public @UnknownNullability Integer unclaimReward(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "unclaim-reward", Comparator.naturalOrder());
    }

    @Override
    public @UnknownNullability Integer freeChunks(@Nullable PlatformUser target) {
        if (this.limits == null) return null;
        return this.limits.getInt(target, "free-chunks", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Boolean enabled() {
        return this.getBoolean("use-economy");
    }

}
