package io.github.wasabithumb.xclaim.integration.economy.essentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.IntegrationInject;
import io.github.wasabithumb.xclaim.integration.economy.EconomyIntegration;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import net.ess3.api.MaxMoneyException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class EssentialsEconomyIntegration implements EconomyIntegration {

    private final Essentials handle;

    @IntegrationInject
    private PlatformTypeAdapter adapter;

    public EssentialsEconomyIntegration() throws IntegrationException {
        try {
            this.handle = Essentials.getPlugin(Essentials.class);
        } catch (Exception e) {
            throw new IntegrationException("Failed to get Essentials plugin", e);
        }
    }

    //

    private @Nullable User adapt(@NotNull PlatformUser user) {
        Object sender = this.adapter.user(user);
        if (sender instanceof Player ply) {
            return this.handle.getUser(ply);
        } else if (sender instanceof OfflinePlayer op) {
            return this.handle.getUser(op.getUniqueId());
        } else {
            return null;
        }
    }

    //

    @Override
    public boolean canAfford(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        User u = this.adapt(user);
        if (u == null) return false;
        return Economy.hasEnough(u, amount);
    }

    @Override
    public boolean canAfford(@NotNull PlatformUser user, double amount) {
        return this.canAfford(user, BigDecimal.valueOf(amount));
    }

    @Override
    public boolean give(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        User u = this.adapt(user);
        if (u == null) return false;
        try {
            Economy.add(u, amount);
        } catch (NoLoanPermittedException | MaxMoneyException | ArithmeticException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean give(@NotNull PlatformUser user, double amount) {
        return this.give(user, BigDecimal.valueOf(amount));
    }

    @Override
    public boolean take(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        User u = this.adapt(user);
        if (u == null) return false;
        try {
            Economy.subtract(u, amount);
        } catch (NoLoanPermittedException | MaxMoneyException | ArithmeticException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean take(@NotNull PlatformUser user, double amount) {
        return this.take(user, BigDecimal.valueOf(amount));
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return Economy.format(amount);
    }

    @Override
    public @NotNull String format(double amount) {
        return this.format(BigDecimal.valueOf(amount));
    }

}
