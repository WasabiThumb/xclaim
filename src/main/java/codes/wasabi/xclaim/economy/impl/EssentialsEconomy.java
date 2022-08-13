package codes.wasabi.xclaim.economy.impl;

import codes.wasabi.xclaim.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class EssentialsEconomy extends Economy {

    private final com.earth2me.essentials.Essentials ess;

    public EssentialsEconomy() throws Exception {
        ess = com.earth2me.essentials.Essentials.getPlugin(com.earth2me.essentials.Essentials.class);
        Class.forName("com.earth2me.essentials.api.Economy");
    }

    private com.earth2me.essentials.User getUser(OfflinePlayer ply) {
        if (ply.isOnline()) {
            if (ply instanceof Player) {
                return ess.getUser((Player) ply);
            }
        }
        return ess.getUser(ply.getUniqueId());
    }

    @Override
    public boolean canAfford(OfflinePlayer ply, BigDecimal amount) {
        return com.earth2me.essentials.api.Economy.hasEnough(getUser(ply), amount);
    }

    @Override
    public boolean give(OfflinePlayer ply, BigDecimal amount) {
        try {
            com.earth2me.essentials.api.Economy.add(getUser(ply), amount);
        } catch (com.earth2me.essentials.api.NoLoanPermittedException | net.ess3.api.MaxMoneyException | ArithmeticException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean take(OfflinePlayer ply, BigDecimal amount) {
        try {
            com.earth2me.essentials.api.Economy.subtract(getUser(ply), amount);
        } catch (com.earth2me.essentials.api.NoLoanPermittedException | net.ess3.api.MaxMoneyException | ArithmeticException e) {
            return false;
        }
        return true;
    }

    @Override
    public @NotNull String format(BigDecimal amount) {
        return com.earth2me.essentials.api.Economy.format(amount);
    }

}
