package codes.wasabi.xclaim.config.struct.sub.integrations;

import codes.wasabi.xclaim.config.struct.helpers.ToggleableConfig;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface EconomyConfig extends ToggleableConfig {

    @UnknownNullability Integer claimPrice(@Nullable Permissible target);

    @UnknownNullability Integer unclaimReward(@Nullable Permissible target);

    @UnknownNullability Integer freeChunks(@Nullable Permissible target);

}
