package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.logging.Level;

public abstract class AbstractXClaimPlugin extends JavaPlugin implements XClaimBootstrap {

    /**
     * <p>
     *     Obtains the active XClaim instance. This is not preferred in favor of using the
     *      {@link org.bukkit.plugin.ServicesManager services manager}.
     * </p>
     * <p>Example:</p>
     * <pre>
     *     {@code XClaim instance = Bukkit.getServicesManager().getRegistration(XClaim.class).getProvider();}
     * </pre>
     * <p>
     *     If your code only works when using this method, it is probably a bug. Please report it.
     * </p>
     */
    @ApiStatus.Obsolete
    public static @UnknownNullability XClaim getInstance() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("XClaim");
        if (plugin instanceof AbstractXClaimPlugin xcp) {
            return xcp.instance;
        }
        return null;
    }

    //

    protected boolean init;
    protected BukkitPlatform platform;
    protected XClaim instance;

    @Override
    public void onEnable() {
        this.init = false;
        this.platform = this.createPlatform();
        this.instance = new XClaim(this);

        try {
            this.instance.enable();
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Fatal error while enabling XClaim", t);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getServicesManager().register(
                XClaim.class,
                this.instance,
                this,
                ServicePriority.High
        );
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregister(XClaim.class, this.instance);

        try {
            this.instance.disable();
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Fatal error while disabling XClaim", t);
        } finally {
            this.platform.destroy();
        }
    }

    //

    @Override
    public @NotNull BukkitPlatform platform() {
        return this.platform;
    }

    //

    protected abstract @NotNull BukkitPlatform createPlatform();

}
