package codes.wasabi.xclaim.config.impl.toml.sub.integrations;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.impl.toml.helpers.TomlGroupableValue;
import codes.wasabi.xclaim.config.struct.helpers.ConfigComparators;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import com.moandjiezana.toml.Toml;
import it.unimi.dsi.fastutil.ints.IntComparators;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlEconomyConfig extends TomlConfig implements EconomyConfig {

    private final TomlGroupableValue.Int claimPrice;
    private final TomlGroupableValue.Int unclaimReward;
    private final TomlGroupableValue.Int freeChunks;
    public TomlEconomyConfig(@Nullable Toml table) {
        super(table);
        this.claimPrice = new TomlGroupableValue.Int(table, "claim-price", IntComparators.OPPOSITE_COMPARATOR);
        this.unclaimReward = new TomlGroupableValue.Int(table, "unclaim-reward", IntComparators.NATURAL_COMPARATOR);
        this.freeChunks = new TomlGroupableValue.Int(table, "free-chunks", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Integer claimPrice(@Nullable Permissible target) {
        return this.claimPrice.get(target);
    }

    @Override
    public @UnknownNullability Integer unclaimReward(@Nullable Permissible target) {
        return this.unclaimReward.get(target);
    }

    @Override
    public @UnknownNullability Integer freeChunks(@Nullable Permissible target) {
        return this.freeChunks.get(target);
    }

}
