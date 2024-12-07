package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.assets.BukkitAssetManager;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.claim.data.impl.yaml.YamlClaimDataManager;
import io.github.wasabithumb.xclaim.config.impl.yaml.YamlRootConfig;
import io.github.wasabithumb.xclaim.config.struct.RootConfig;
import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.trust.TrustManager;
import io.github.wasabithumb.xclaim.trust.impl.yaml.YamlTrustManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    protected BukkitAssetManager assets;
    protected XClaim instance;

    @Override
    public void onEnable() {
        this.init = false;
        this.platform = this.createPlatform();
        this.assets = new BukkitAssetManager(this);
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

    @Override
    public @NotNull BukkitAssetManager assets() {
        return this.assets;
    }

    //

    protected abstract @NotNull BukkitPlatform createPlatform();

    @Override
    public @NotNull Logger logger() {
        return super.getLogger();
    }

    @Override
    public boolean supportsLegacyConfig() {
        return true;
    }

    @Override
    public @NotNull RootConfig loadLegacyConfig(@NotNull InputStream root) throws Exception {
        YamlConfiguration handle = new YamlConfiguration();
        handle.load(new InputStreamReader(root, StandardCharsets.UTF_8));
        return new YamlRootConfig(handle);
    }

    @Override
    public boolean supportsLegacyTrust() {
        return true;
    }

    @Override
    public @NotNull TrustManager loadLegacyTrust(@NotNull File file) throws Exception {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(file);
        return new YamlTrustManager(file, cfg);
    }

    @Override
    public boolean supportsLegacyClaimData() {
        return true;
    }

    @Override
    public @NotNull ClaimDataManager loadLegacyClaimData(@NotNull File file) throws Exception {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(file);
        return new YamlClaimDataManager(file, cfg);
    }

}
