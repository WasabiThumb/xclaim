package io.github.wasabithumb.xclaim.integration.economy.vault;

import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.economy.EconomyIntegration;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class VaultEconomyIntegration implements EconomyIntegration {

    private final Economy eco;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    //

    public VaultEconomyIntegration() throws IntegrationException {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null)
            throw new IntegrationException("No service registration");

        Economy eco = rsp.getProvider();
        if (!eco.isEnabled())
            throw new IntegrationException("Service provider is not enabled");

        this.eco = eco;
    }

    @Override
    public int weight() {
        return 1;
    }

    //

    private @Nullable OfflinePlayer adapt(@Nullable PlatformUser user) {
        Object sender = this.adapter.user(user);
        if (sender instanceof OfflinePlayer op) return op;
        return null;
    }

    //

    @Override
    public boolean canAfford(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        return this.canAfford(user, amount.doubleValue());
    }

    @Override
    public boolean canAfford(@NotNull PlatformUser user, double amount) {
        OfflinePlayer ply = this.adapt(user);
        if (ply == null) return false;
        return this.eco.has(ply, amount);
    }

    @Override
    public boolean give(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        return this.give(user, amount.doubleValue());
    }

    @Override
    public boolean give(@NotNull PlatformUser user, double amount) {
        OfflinePlayer ply = this.adapt(user);
        if (ply == null) return false;
        return this.eco.depositPlayer(ply, amount)
                .transactionSuccess();
    }

    @Override
    public boolean take(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        return this.take(user, amount.doubleValue());
    }

    @Override
    public boolean take(@NotNull PlatformUser user, double amount) {
        OfflinePlayer ply = this.adapt(user);
        if (ply == null) return false;
        return this.eco.withdrawPlayer(ply, amount)
                .transactionSuccess();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return this.format(amount.doubleValue());
    }

    @Override
    public @NotNull String format(double amount) {
        return this.eco.format(amount);
    }

}
