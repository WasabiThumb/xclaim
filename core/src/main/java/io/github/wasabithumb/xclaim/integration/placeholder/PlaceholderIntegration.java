package io.github.wasabithumb.xclaim.integration.placeholder;

import io.github.wasabithumb.xclaim.integration.Integration;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderRegistry;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderIntegration extends Integration {

    @NotNull PlaceholderRegistry registry();

    default @Nullable String resolve(@NotNull PlatformUser user, @NotNull String key) {
        PlaceholderArgumentQueue args = PlaceholderArgumentQueue.of(key);
        CharSequence first = args.poll();
        if (first == null || CharSequence.compare(first, "xclaim") != 0) return null;
        return this.registry().resolve(user, args);
    }

}
