package io.github.wasabithumb.xclaim.config.impl.toml.sub.integrations;

import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.impl.toml.helpers.TomlGroupableValue;
import io.github.wasabithumb.xclaim.config.struct.helpers.ConfigComparators;
import io.github.wasabithumb.xclaim.config.struct.sub.integrations.EconomyConfig;
import com.moandjiezana.toml.Toml;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Comparator;

public final class TomlEconomyConfig extends TomlConfig implements EconomyConfig {

    private final TomlGroupableValue.Int claimPrice;
    private final TomlGroupableValue.Int unclaimReward;
    private final TomlGroupableValue.Int freeChunks;
    public TomlEconomyConfig(@Nullable Toml table) {
        super(table);
        this.claimPrice = new TomlGroupableValue.Int(table, "claim-price", Comparator.reverseOrder());
        this.unclaimReward = new TomlGroupableValue.Int(table, "unclaim-reward", Comparator.naturalOrder());
        this.freeChunks = new TomlGroupableValue.Int(table, "free-chunks", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Integer claimPrice(@Nullable PlatformUser target) {
        return this.claimPrice.get(target);
    }

    @Override
    public @UnknownNullability Integer unclaimReward(@Nullable PlatformUser target) {
        return this.unclaimReward.get(target);
    }

    @Override
    public @UnknownNullability Integer freeChunks(@Nullable PlatformUser target) {
        return this.freeChunks.get(target);
    }

}
