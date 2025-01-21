package io.github.wasabithumb.xclaim.config.sub.integrations;

import io.github.wasabithumb.xclaim.config.helpers.ToggleableConfig;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface EconomyConfig extends ToggleableConfig {

    @UnknownNullability Integer claimPrice(@Nullable PlatformUser target);

    @UnknownNullability Integer unclaimReward(@Nullable PlatformUser target);

    @UnknownNullability Integer freeChunks(@Nullable PlatformUser target);

}
