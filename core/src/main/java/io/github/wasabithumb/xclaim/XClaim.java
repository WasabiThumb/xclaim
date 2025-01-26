package io.github.wasabithumb.xclaim;

import com.moandjiezana.toml.Toml;
import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.claim.data.impl.sqlite.SQLiteClaimDataManager;
import io.github.wasabithumb.xclaim.command.CommandManager;
import io.github.wasabithumb.xclaim.config.impl.defaulting.DefaultingRootConfig;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlRootConfig;
import io.github.wasabithumb.xclaim.config.RootConfig;
import io.github.wasabithumb.xclaim.gui.GuiManager;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.integration.Integrations;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUserManager;
import io.github.wasabithumb.xclaim.routine.Routines;
import io.github.wasabithumb.xclaim.trust.TrustManager;
import io.github.wasabithumb.xclaim.trust.impl.sqlite.SQLiteTrustManager;
import io.github.wasabithumb.xclaim.util.io.stream.StreamUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XClaim {

    private final XClaimBootstrap bootstrap;
    private RootConfig rootConfig = null;
    private Lang lang = null;
    private Integrations integrations = null;
    private TrustManager trust = null;
    private ClaimManager claims = null;
    private GuiManager gui = null;
    private CommandManager commands = null;
    private Routines routines = null;

    @ApiStatus.Internal
    XClaim(@NotNull XClaimBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @ApiStatus.Internal
    public @NotNull XClaimBootstrap bootstrap() {
        return this.bootstrap;
    }

    public @NotNull Platform platform() {
        return this.bootstrap.platform();
    }

    public @NotNull AssetManager assets() {
        return this.bootstrap.assets();
    }

    public @NotNull Logger logger() {
        return this.bootstrap.logger();
    }

    //

    public @NotNull RootConfig rootConfig() {
        return this.rootConfig;
    }

    public @NotNull Lang lang() {
        return this.lang;
    }

    public @NotNull String lang(@NotNull Translatable key) {
        return key.format(this.lang);
    }

    public @NotNull TrustManager trust() {
        return this.trust;
    }

    public @NotNull ClaimManager claims() {
        return this.claims;
    }

    public @NotNull Integrations integrations() {
        return this.integrations;
    }

    public @NotNull GuiManager gui() {
        return this.gui;
    }

    public @NotNull CommandManager commands() {
        return this.commands;
    }

    //

    void enable() {
        this.loadRootConfig();
        this.loadLang();
        if (this.rootConfig.isLegacy()) {
            // Warn about usage of the legacy config
            this.adminBroadcast(this.lang(I18N.CONFIG_MIGRATION_LINE1));
            this.adminBroadcast(this.lang(I18N.CONFIG_MIGRATION_LINE2));
        }
        this.loadIntegrations();
        this.loadTrust();
        this.loadClaims();
        this.loadGUI();
        this.loadCommands();
        this.loadRoutines();
        this.logger().log(Level.INFO, this.lang(I18N.STARTUP_DONE));
    }

    void disable() {
        this.routines.stop();
        this.gui.stop();
        this.integrations.shutdown();
        try {
            this.claims.close();
        } catch (Exception e) {
            this.logger().log(Level.WARNING, "Exception while shutting down claim manager", e);
        }
        try {
            this.trust.close();
        } catch (Exception e) {
            this.logger().log(Level.WARNING, "Exception while shutting down trust manager", e);
        }
        this.logger().log(Level.INFO, this.lang(I18N.DISABLE_DONE));
    }

    /* STARTUP TASKS */

    private void loadRootConfig() {
        RootConfig cfg;
        try {
            cfg = this.loadRootConfigInternal();
        } catch (IOException e) {
            throw new AssertionError("Failed to load configuration", e);
        }
        this.rootConfig = new DefaultingRootConfig(cfg);
    }

    private @NotNull RootConfig loadRootConfigInternal() throws IOException {
        final AssetSource data = this.assets().data();
        final AssetSource resources = this.assets().resources();

        // Copy bundled config.toml to disk if it doesn't already exist
        if (!data.exists("config.toml")) {
            try (
                    InputStream is = resources.read("config.toml");
                    OutputStream os = data.write("config.toml")
            ) {
                StreamUtil.pipe(is, os);
            }
        }

        // Check for & load legacy config
        Throwable suppressed = null;
        if (data.exists("config.yml") && this.bootstrap.supportsLegacyConfig()) {
            try {
                return this.bootstrap.loadLegacyConfig(data.read("config.yml"));
            } catch (Exception e) {
                suppressed = new AssertionError(
                        "Failed to load legacy config",
                        e
                );
            }
        }

        // Load non-legacy config
        Toml toml = new Toml();
        try (InputStream is = data.read("config.toml");
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)
        ) {
            toml.read(reader);
        } catch (Exception e) {
            AssertionError ex = new AssertionError("Failed to read config.toml", e);
            if (suppressed != null) ex.addSuppressed(suppressed);
            throw ex;
        }
        return new TomlRootConfig(toml);
    }

    private void loadLang() {
        final String target = this.rootConfig.language();
        Lang lang;
        try {
            lang = this.loadLangInternal(target);
        } catch (IOException e) {
            throw new AssertionError("Failed to load language pack (targeting " + target + ")", e);
        }
        this.lang = lang;
    }

    private @NotNull Lang loadLangInternal(@NotNull String target) throws IOException {
        final AssetSource live = this.assets().data().sub("lang");
        final AssetSource bundled = this.assets().resources().sub("lang");
        Lang ret = null;

        // List bundled language packs, to see if the data directory is missing any.
        // If the target language pack matches a bundled language pack, update the live copy with any new keys that may
        // have been added between releases.
        for (String name : bundled.list(false, true)) {
            if (name.length() < 6 || !name.endsWith(".json")) continue;
            String id = name.substring(0, name.length() - 5);

            if (!live.exists(name)) {
                try (InputStream is = bundled.read(name);
                     OutputStream os = live.write(name)
                ) {
                    StreamUtil.pipe(is, os);
                }
                continue;
            }
            if (!id.equalsIgnoreCase(target)) continue;

            ret = new Lang(id);
            try (InputStream is = live.read(name)) {
                ret.load(is);
            }
            try (InputStream is = bundled.read(name)) {
                if (!ret.load(is)) continue;
            }
            try (OutputStream os = live.write(name)) {
                ret.serialize(os);
                os.flush();
            }
        }
        if (ret != null) return ret;

        ret = new Lang(target);
        try (InputStream is = live.read(target + ".json")) {
            ret.load(is);
        }
        return ret;
    }

    private void loadTrust() {
        this.logger().log(Level.INFO, this.lang(I18N.TRUST_LOAD));
        Throwable loadError = null;

        // Try loading legacy
        File legacy = this.assets().data().resolve("trust.yml");
        if (this.bootstrap.supportsLegacyTrust() && legacy.isFile()) {
            try {
                this.trust = this.bootstrap.loadLegacyTrust(legacy);
                return;
            } catch (Exception e) {
                loadError = e;
            }
        }

        // Try loading modern
        File modern = this.assets().data().resolve("trust.db");
        try {
            this.trust = new SQLiteTrustManager(modern, this.logger());
            return;
        } catch (Exception e) {
            if (loadError != null) e.addSuppressed(loadError);
            loadError = e;
        }

        this.logger().log(Level.WARNING, this.lang(I18N.TRUST_LOAD_ERR), loadError);
    }

    private void loadClaims() {
        ClaimDataManager cdm = this.loadClaimData();
        if (cdm == null) return;
        this.claims = new ClaimManager(this, cdm);
        this.claims.load();
    }

    private @Nullable ClaimDataManager loadClaimData() {
        Throwable loadError = null;

        // Try loading legacy
        File legacy = this.assets().data().resolve("claims.yml");
        if (this.bootstrap.supportsLegacyClaimData() && legacy.isFile()) {
            try {
                return this.bootstrap.loadLegacyClaimData(legacy);
            } catch (Exception e) {
                loadError = e;
            }
        }

        // Try loading modern
        File modern = this.assets().data().resolve("claims.db");
        try {
            return new SQLiteClaimDataManager(modern, this.logger());
        } catch (Exception e) {
            if (loadError != null) e.addSuppressed(loadError);
            loadError = e;
        }

        this.logger().log(Level.WARNING, this.lang(I18N.CLAIMS_LOAD_ERR), loadError);
        return null;
    }

    private void loadIntegrations() {
        this.integrations = new Integrations(this);
        this.integrations.startup();
    }

    private void loadGUI() {
        this.gui = new GuiManager(this);
        this.gui.start();
    }

    private void loadCommands() {
        this.commands = new CommandManager(this);
    }

    private void loadRoutines() {
        this.routines = new Routines(this);
        this.routines.start();
    }

    //

    /** Send a message to the console and all online operators. */
    private void adminBroadcast(@NotNull String message) {
        PlatformUserManager users = this.platform().users();
        users.console().sendMessage(message);
        for (PlatformPlayer ply : users.players()) {
            if (!ply.isOp()) continue;
            ply.sendMessage(message);
        }
    }

}
