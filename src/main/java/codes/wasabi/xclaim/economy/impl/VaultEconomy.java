package codes.wasabi.xclaim.economy.impl;

import codes.wasabi.xclaim.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public class VaultEconomy extends Economy {

    private final net.milkbowl.vault.economy.Economy eco;

    public VaultEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class));
        eco = requireNonNull(rsp.getProvider());
    }

    @Override
    public boolean canAfford(OfflinePlayer ply, BigDecimal amount) {
        return eco.has(ply, amount.doubleValue());
    }

    @Override
    public boolean give(OfflinePlayer ply, BigDecimal amount) {
        return eco.depositPlayer(ply, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean take(OfflinePlayer ply, BigDecimal amount) {
        return eco.withdrawPlayer(ply, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public @NotNull String format(BigDecimal amount) {
        return eco.format(amount.doubleValue());
    }

}
