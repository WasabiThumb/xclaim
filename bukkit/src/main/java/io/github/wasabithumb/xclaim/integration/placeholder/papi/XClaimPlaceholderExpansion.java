package io.github.wasabithumb.xclaim.integration.placeholder.papi;

import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
final class XClaimPlaceholderExpansion extends PlaceholderExpansion {

    private final PAPIPlaceholderIntegration integration;
    XClaimPlaceholderExpansion(@NotNull PAPIPlaceholderIntegration integration) {
        this.integration = integration;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "xclaim";
    }

    @Override
    public @NotNull String getAuthor() {
        return "WasabiThumb & Contributors";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        return this.resolveInternal(player, params, false);
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        return this.resolveInternal(player, params, true);
    }

    private @Nullable String resolveInternal(
            @Nullable OfflinePlayer player,
            @NotNull String params,
            boolean definitelyOnline
    ) {
        PlaceholderArgumentQueue args = PlaceholderArgumentQueue.of(params);
        CharSequence first = args.poll();
        if (first == null || CharSequence.compare(first, "xclaim") != 0) return null;

        PlatformUser user;
        if (player == null) {
            user = this.integration.runtime.platform().users().console();
        } else {
            if (definitelyOnline || player instanceof Player) {
                user = this.integration.adapter.player(player);
            } else {
                user = this.integration.adapter.offlineUser(player);
            }
        }

        return this.integration.registry.resolve(user, args);
    }

}
