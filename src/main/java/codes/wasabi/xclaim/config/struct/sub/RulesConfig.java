package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface RulesConfig extends Config {

    @UnknownNullability Integer placementRaw();

    default @UnknownNullability PlacementRule placement() {
        Integer val = this.placementRaw();
        if (val == null) return null;
        return PlacementRule.of(val);
    }

    @UnknownNullability Integer minDistance();

    @UnknownNullability Boolean exemptOwner();

    @UnknownNullability Integer maxChunks(@Nullable Permissible target);

    @UnknownNullability Integer maxClaims(@Nullable Permissible target);

    @UnknownNullability Integer maxClaimsInWorld(@Nullable Permissible target);

    //

    enum PlacementRule {
        NONE(0),
        CARDINAL(1),
        NEIGHBOR(2);

        private final int code;
        PlacementRule(int code) {
            this.code = code;
        }

        public final int code() {
            return this.code;
        }

        public static @NotNull PlacementRule of(int code) {
            if (code <= 0) return NONE;
            if (code >= 2) return NEIGHBOR;
            return CARDINAL;
        }
    }

}
