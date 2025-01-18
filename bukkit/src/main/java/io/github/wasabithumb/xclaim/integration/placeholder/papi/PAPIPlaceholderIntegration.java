package io.github.wasabithumb.xclaim.integration.placeholder.papi;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.placeholder.PlaceholderIntegration;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderArgumentQueue;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderRegistry;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIPlaceholderIntegration implements PlaceholderIntegration {

    @IntegrationInject
    XClaim runtime;

    @IntegrationInject
    PlatformTypeAdapter adapter;

    PlaceholderRegistry registry;

    private final Expansion expansion;

    //

    public PAPIPlaceholderIntegration() throws IntegrationException {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            throw new IntegrationException("PAPI is not enabled");
        this.expansion = new Expansion(this);
    }

    //

    @Override
    public void onEnable() {
        this.registry = new PlaceholderRegistry(this.runtime);
        this.expansion.register();
    }

    @Override
    public void onDisable() {
        this.expansion.unregister();
    }

    @Override
    public @NotNull PlaceholderRegistry registry() {
        return this.registry;
    }

    @Override
    public @Nullable String resolve(@NotNull PlatformUser user, @NotNull String key) {
        if (user.isPlayer()) {
            Player ply = (Player) this.adapter.player(user.asPlayer());
            return PlaceholderAPI.setPlaceholders(ply, "%" + key + "%");
        }
        if (user instanceof PlatformOfflineUser offline) {
            OfflinePlayer op = (OfflinePlayer) this.adapter.offlineUser(offline);
            return PlaceholderAPI.setPlaceholders(op, "%" + key + "%");
        }
        return null;
    }

    //

    private static final class Expansion extends PlaceholderExpansion {

        private final PAPIPlaceholderIntegration integration;
        Expansion(@NotNull PAPIPlaceholderIntegration integration) {
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

}
