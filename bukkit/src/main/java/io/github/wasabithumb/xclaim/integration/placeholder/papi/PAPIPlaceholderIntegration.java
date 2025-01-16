package io.github.wasabithumb.xclaim.integration.placeholder.papi;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.placeholder.PlaceholderIntegration;
import io.github.wasabithumb.xclaim.placeholder.PlaceholderRegistry;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import me.clip.placeholderapi.PlaceholderAPI;
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

    private final XClaimPlaceholderExpansion expansion;

    //

    public PAPIPlaceholderIntegration() throws IntegrationException {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            throw new IntegrationException("PAPI is not enabled");
        this.expansion = new XClaimPlaceholderExpansion(this);
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

}
