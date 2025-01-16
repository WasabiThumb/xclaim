package io.github.wasabithumb.xclaim.placeholder;

import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Placeholder {

    @NotNull String key();

    @Nullable String resolve(@NotNull PlatformUser user, @NotNull PlaceholderArgumentQueue args);

}
