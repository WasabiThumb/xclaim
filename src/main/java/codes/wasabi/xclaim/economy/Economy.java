package codes.wasabi.xclaim.economy;

import codes.wasabi.xclaim.XClaim;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Economy {

    private static boolean checked = false;
    private static boolean has = false;
    private static Economy instance = null;

    public static boolean isAvailable() {
        if (!checked) get();
        return has;
    }

    private static void internalGet() {
        try {
            instance = new codes.wasabi.xclaim.economy.impl.VaultEconomy();
            return;
        } catch (Throwable ignored) {
        }
        try {
            instance = new codes.wasabi.xclaim.economy.impl.EssentialsEconomy();
        } catch (Throwable ignored) {
        }
    }

    public static @Nullable Economy get() {
        if (checked) return instance;
        checked = true;
        instance = null;
        if (XClaim.mainConfig.getBoolean("use-economy", false)) {
            internalGet();
        }
        has = (instance != null);
        return instance;
    }

    public static @NotNull Economy getAssert() {
        return Objects.requireNonNull(get());
    }

    public abstract boolean canAfford(OfflinePlayer ply, BigDecimal amount);

    public abstract boolean give(OfflinePlayer ply, BigDecimal amount);

    public abstract boolean take(OfflinePlayer ply, BigDecimal amount);

    public abstract @NotNull String format(BigDecimal amount);

    public boolean transfer(OfflinePlayer from, OfflinePlayer to, BigDecimal amount) {
        if (take(from, amount)) {
            if (give(to, amount)) {
                return true;
            } else {
                give(from, amount);
            }
        }
        return false;
    }

}
