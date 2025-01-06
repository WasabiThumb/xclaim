package io.github.wasabithumb.xclaim.integration.economy;

import io.github.wasabithumb.xclaim.integration.Integration;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public interface EconomyIntegration extends Integration {

    boolean canAfford(@NotNull PlatformUser user, @NotNull BigDecimal amount);

    boolean canAfford(@NotNull PlatformUser user, double amount);

    boolean give(@NotNull PlatformUser user, @NotNull BigDecimal amount);

    boolean give(@NotNull PlatformUser user, double amount);

    boolean take(@NotNull PlatformUser user, @NotNull BigDecimal amount);

    boolean take(@NotNull PlatformUser user, double amount);

    @NotNull String format(@NotNull BigDecimal amount);

    @NotNull String format(double amount);

    default boolean transfer(@NotNull PlatformUser from, @Nullable PlatformUser to, @NotNull BigDecimal amount) {
        if (this.take(from, amount)) {
            if (to == null || to instanceof PlatformConsoleUser || this.give(to, amount)) {
                return true;
            } else {
                this.give(from, amount);
            }
        }
        return false;
    }

    default boolean transfer(@NotNull PlatformUser from, @Nullable PlatformUser to, double amount) {
        if (this.take(from, amount)) {
            if (to == null || to instanceof PlatformConsoleUser || this.give(to, amount)) {
                return true;
            } else {
                this.give(from, amount);
            }
        }
        return false;
    }

}
