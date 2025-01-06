package io.github.wasabithumb.xclaim.integration;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.config.struct.sub.IntegrationsConfig;
import io.github.wasabithumb.xclaim.integration.economy.EconomyIntegration;
import io.github.wasabithumb.xclaim.integration.map.MapIntegration;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionIntegration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Accessor for integrations
 * @see #map()
 */
public final class Integrations {

    private static final int MAP        = 0b0001;
    private static final int PROTECTION = 0b0010;
    private static final int ECONOMY    = 0b0100;

    //

    private final MapIntegration map;
    private final ProtectionIntegration protection;
    private final EconomyIntegration economy;
    private final int flags;
    private boolean init;

    @ApiStatus.Internal
    public Integrations(@NotNull XClaim runtime) {
        final IntegrationsConfig cfg = runtime.rootConfig().integrations();
        int flags = 0;

        MapIntegration map;
        if (cfg.map().enabled()) {
            map = Integration.load(MapIntegration.class, runtime);
            if (map != null) flags |= MAP;
        } else {
            map = null;
        }

        ProtectionIntegration protection;
        if (cfg.protection().enabled()) {
            protection = Integration.load(ProtectionIntegration.class, runtime);
            if (protection != null) flags |= PROTECTION;
        } else {
            protection = null;
        }

        EconomyIntegration economy;
        if (cfg.economy().enabled()) {
            economy = Integration.load(EconomyIntegration.class, runtime);
            if (economy != null) flags |= ECONOMY;
        } else {
            economy = null;
        }

        this.map = map;
        this.protection = protection;
        this.economy = economy;
        this.flags = flags;
        this.init = false;
    }

    @ApiStatus.Internal
    public synchronized void startup() {
        if (this.init) return;
        for (Integration i : this.all())
            i.onEnable();
        this.init = true;
    }

    @ApiStatus.Internal
    public synchronized void shutdown() {
        if (!this.init) return;
        for (Integration i : this.all())
            i.onDisable();
        this.init = false;
    }

    /**
     * Returns a new array containing all successfully loaded integrations.
     */
    @Contract(" -> new")
    public @NotNull Integration @NotNull [] all() {
        final Integration[] ret = new Integration[Integer.bitCount(this.flags)];
        int head = 0;

        if (this.hasMap())
            ret[head++] = this.map;

        if (this.hasProtection())
            ret[head++] = this.protection;

        if (this.hasEconomy())
            ret[head] = this.economy;

        return ret;
    }

    //

    /**
     * Basic way to check if a given integration is supported and loaded successfully.
     * @param flag Flag corresponding to the given integration; flags are stored as constants on this class
     */
    private boolean has(int flag) {
        return (this.flags & flag) != 0;
    }

    //

    /**
     * The map (e.g. BlueMap) integration. Will be non-null if {@link #hasMap()} is true.
     */
    public @UnknownNullability MapIntegration map() {
        return this.map;
    }

    /**
     * @return True if {@link #map()} is non-null.
     */
    public boolean hasMap() {
        return this.has(MAP);
    }

    /**
     * The protection (e.g. WorldGuard) integration. Will be non-null if {@link #hasProtection()} is true.
     */
    public @UnknownNullability ProtectionIntegration protection() {
        return this.protection;
    }

    /**
     * @return True if {@link #protection()} is non-null.
     */
    public boolean hasProtection() {
        return this.has(PROTECTION);
    }

    /**
     * The economy (e.g. Vault) integration. Will be non-null if {@link #hasEconomy()} is true.
     */
    public @UnknownNullability EconomyIntegration economy() {
        return this.economy;
    }

    /**
     * @return True if {@link #protection()} is non-null.
     */
    public boolean hasEconomy() {
        return this.has(ECONOMY);
    }

}
