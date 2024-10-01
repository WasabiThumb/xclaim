package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.impl.toml.helpers.TomlGroupableValue;
import codes.wasabi.xclaim.config.struct.helpers.ConfigComparators;
import codes.wasabi.xclaim.config.struct.sub.RulesConfig;
import com.moandjiezana.toml.Toml;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlRulesConfig extends TomlConfig implements RulesConfig {

    private final TomlGroupableValue.Int maxChunks;
    private final TomlGroupableValue.Int maxClaims;
    private final TomlGroupableValue.Int maxClaimsInWorld;
    public TomlRulesConfig(@Nullable Toml table) {
        super(table);
        this.maxChunks = new TomlGroupableValue.Int(table, "max-chunks", ConfigComparators.INT_NATURAL_OR_INF);
        this.maxClaims = new TomlGroupableValue.Int(table, "max-claims", ConfigComparators.INT_NATURAL_OR_INF);
        this.maxClaimsInWorld = new TomlGroupableValue.Int(table, "max-claims-in-world", ConfigComparators.INT_NATURAL_OR_INF);
    }

    @Override
    public @UnknownNullability Integer placementRaw() {
        return this.getInt("placement");
    }

    @Override
    public @UnknownNullability Integer minDistance() {
        return this.getInt("min-distance");
    }

    @Override
    public @UnknownNullability Boolean exemptOwner() {
        return this.getBoolean("exempt-owner");
    }

    @Override
    public @UnknownNullability Integer maxChunks(@Nullable Permissible target) {
        return this.maxChunks.get(target);
    }

    @Override
    public @UnknownNullability Integer maxClaims(@Nullable Permissible target) {
        return this.maxClaims.get(target);
    }

    @Override
    public @UnknownNullability Integer maxClaimsInWorld(@Nullable Permissible target) {
        return this.maxClaimsInWorld.get(target);
    }

}
