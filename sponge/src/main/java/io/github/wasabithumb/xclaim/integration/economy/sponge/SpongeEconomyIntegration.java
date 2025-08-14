package io.github.wasabithumb.xclaim.integration.economy.sponge;

import io.github.wasabithumb.xclaim.integration.IntegrationException;
import io.github.wasabithumb.xclaim.integration.economy.EconomyIntegration;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;
import java.util.Optional;

public final class SpongeEconomyIntegration implements EconomyIntegration {

    private final EconomyService service;

    public SpongeEconomyIntegration() throws IntegrationException {
        Optional<EconomyService> optional = Sponge.server().serviceProvider().economyService();
        if (optional.isEmpty()) throw new IntegrationException("No economy service registered");
        this.service = optional.get();
    }

    //

    @Override
    public boolean canAfford(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        Optional<UniqueAccount> optional = this.service.findOrCreateAccount(user.uuid());
        if (optional.isEmpty()) return false;
        UniqueAccount account = optional.get();
        return account.balance(this.service.defaultCurrency()).compareTo(amount) >= 0;
    }

    @Override
    public boolean canAfford(@NotNull PlatformUser user, double amount) {
        return this.canAfford(user, BigDecimal.valueOf(amount));
    }

    @Override
    public boolean give(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        Optional<UniqueAccount> optional = this.service.findOrCreateAccount(user.uuid());
        if (optional.isEmpty()) return false;
        UniqueAccount account = optional.get();
        return account.deposit(this.service.defaultCurrency(), amount).result() == ResultType.SUCCESS;
    }

    @Override
    public boolean give(@NotNull PlatformUser user, double amount) {
        return this.give(user, BigDecimal.valueOf(amount));
    }

    @Override
    public boolean take(@NotNull PlatformUser user, @NotNull BigDecimal amount) {
        Optional<UniqueAccount> optional = this.service.findOrCreateAccount(user.uuid());
        if (optional.isEmpty()) return false;
        UniqueAccount account = optional.get();
        return account.withdraw(this.service.defaultCurrency(), amount).result() == ResultType.SUCCESS;
    }

    @Override
    public boolean take(@NotNull PlatformUser user, double amount) {
        return this.take(user, BigDecimal.valueOf(amount));
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return PlainTextComponentSerializer.plainText().serialize(this.service.defaultCurrency().format(amount));
    }

    @Override
    public @NotNull String format(double amount) {
        return this.format(BigDecimal.valueOf(amount));
    }

}
