package io.github.wasabithumb.xclaim;

import com.moandjiezana.toml.Toml;
import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.claim.data.impl.sqlite.SQLiteClaimDataManager;
import io.github.wasabithumb.xclaim.config.impl.defaulting.DefaultingRootConfig;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlRootConfig;
import io.github.wasabithumb.xclaim.config.struct.RootConfig;
import io.github.wasabithumb.xclaim.debug.Debuggable;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUserManager;
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

@Debuggable
public class XClaim {

    private final XClaimBootstrap bootstrap;
    private RootConfig rootConfig = null;
    private Lang lang = null;
    private TrustManager trust = null;
    private ClaimManager claims = null;

    @ApiStatus.Internal
    XClaim(@NotNull XClaimBootstrap bootstrap) {
        this.bootstrap = bootstrap;
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

    public @NotNull String lang(@NotNull String key, @NotNull String @NotNull ... args) {
        return this.lang.get(key, args);
    }

    public @NotNull TrustManager trust() {
        return this.trust;
    }

    public @NotNull ClaimManager claims() {
        return this.claims;
    }

    //

    void enable() {
        this.loadRootConfig();
        this.loadLang();
        if (this.rootConfig.isLegacy()) {
            // Warn about usage of the legacy config
            this.adminBroadcast(this.lang("config-migration-line1"));
            this.adminBroadcast(this.lang("config-migration-line2"));
        }
        // TODO: Integrations
        this.loadTrust();
        this.loadClaims();
    }

    void disable() {
    }

    //

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
        this.logger().log(Level.INFO, this.lang("trust-load"));
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

        this.logger().log(Level.WARNING, this.lang("trust-load-err"), loadError);
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

        this.logger().log(Level.WARNING, this.lang("claims-load-err"), loadError);
        return null;
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

    /*

    public static XClaim instance;
    public static Logger logger;
    public static File trustFile;
    public static YamlConfiguration trustConfig;
    public static File claimsFile;
    public static YamlConfiguration claimsConfig;
    public static RootConfig mainConfig;
    public static CommandManager commandManager;
    public static File jarFile;
    public static File dataFolder;
    public static Lang lang;
    public static GuiService gui;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        loadMainConfig();
        setupLang();
        Platform.init();
        if (mainConfig.isLegacy()) warnConfigMigration();
        if (!Economy.isAvailable()) {
            if (mainConfig.integrations().economy().enabled()) {
                logger.log(Level.WARNING, lang.get("eco-fail"));
            }
        }
        dataFolder = getDataFolder();
        if (dataFolder.mkdirs()) logger.log(Level.INFO, lang.get("data-folder-created"));
        ParticleService.init();
        locateJarFile();
        loadDynmap();
        loadTrustedPlayers();
        loadClaims();
        startServices();
        logger.log(Level.INFO, lang.get("startup-done"));
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        saveTrustedPlayers();
        saveClaims(false);
        stopServices();
        Platform.cleanup();
        logger.log(Level.INFO, lang.get("disable-done"));
    }

    private void locateJarFile() {
        logger.log(Level.INFO, lang.get("locating-jar"));
        jarFile = new File(XClaim.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    private void loadMainConfig() {
        final File dataDir = this.getDataFolder();
        final boolean dataDirExists = dataDir.isDirectory();
        final File config = new File(dataDir, "config.toml");
        final File legacyConfig = new File(dataDir, "config.yml");

        mainConfig = new DefaultingRootConfig(
                loadMainConfigInternal(dataDir, dataDirExists, config, legacyConfig)
        );
    }

    private @NotNull RootConfig loadMainConfigInternal(File dataDir, boolean dataDirExists, File config, File legacyConfig) {
        if (!dataDirExists && (!dataDir.mkdirs()))
            throw new AssertionError("Data directory does not exist and could not be created");

        // Copy bundled config.toml to disk if it doesn't already exist
        if (!dataDirExists || !config.isFile()) {
            try (
                    InputStream is = Objects.requireNonNull(this.getResource("config.toml"));
                    OutputStream os = new FileOutputStream(config, false)
            ) {
                IOUtils.copyLarge(is, os);
            } catch (IOException e) {
                throw new AssertionError("Failed to write config.toml to disk", e);
            }
        }

        // Check for & load legacy config
        Throwable suppressed = null;
        if (dataDirExists && legacyConfig.isFile()) {
            try {
                YamlConfiguration handle = new YamlConfiguration();
                handle.load(legacyConfig);
                return new YamlRootConfig(handle);
            } catch (Exception e) {
                suppressed = new AssertionError(
                        "Failed to load config file \"" + legacyConfig.getName() + "\"",
                        e
                );
            }
        }

        // Load non-legacy config
        Toml toml = new Toml();
        try (InputStream is = new FileInputStream(config);
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

    private static final String[] bundledLangs = new String[] {
            "en-US", "de", "zh", "tr"
    };
    private void setupLang() {
        String l = mainConfig.language();
        File f = getDataFolder();
        File langFolder = new File(f, "lang");
        if (!langFolder.exists()) {
            try {
                if (!langFolder.mkdirs()) throw new IOException("Could not create directory");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!langFolder.isDirectory()) {
            try {
                FileUtils.forceDelete(langFolder);
                if (!langFolder.mkdirs()) throw new IOException("Could not create directory");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        for (String bundled : bundledLangs) {
            File bundledFile = new File(langFolder, bundled + ".json");
            JsonObject curJson;
            boolean exists = bundledFile.exists();
            if (exists) {
                try {
                    curJson = gson.fromJson(new InputStreamReader(new FileInputStream(bundledFile), StandardCharsets.UTF_8), JsonObject.class);
                } catch (Exception e) {
                    curJson = new JsonObject();
                }
            } else {
                curJson = new JsonObject();
                try {
                    if (!bundledFile.createNewFile()) {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    XClaim.logger.log(Level.WARNING, "Failed to create \"" + bundledFile.getPath() + "\", continuing...");
                }
            }
            try {
                boolean canCopyVerbatim = true;
                byte[] verbatim;
                try (InputStream is = Objects.requireNonNull(getResource("lang/" + bundled + ".json"))) {
                    verbatim = StreamUtil.readAllBytes(is);
                    if (exists) {
                        JsonObject model = gson.fromJson(new String(verbatim, StandardCharsets.UTF_8), JsonObject.class);
                        for (Map.Entry<String, JsonElement> entry : model.entrySet()) {
                            String key = entry.getKey();
                            if (!curJson.has(key)) {
                                curJson.add(key, entry.getValue());
                            } else {
                                if (!Objects.equals(curJson.get(key), entry.getValue())) canCopyVerbatim = false;
                            }
                        }
                    }
                }
                try (OutputStream os = new FileOutputStream(bundledFile, false)) {
                    if (canCopyVerbatim) {
                        os.write(verbatim);
                    } else {
                        String json = gson.toJson(curJson);
                        os.write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    os.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File langToUse = new File(langFolder, l + ".json");
        if (!langToUse.exists()) {
            langToUse = new File(langFolder, "en-US.json");
        }
        try (InputStream is = new FileInputStream(langToUse)) {
            byte[] bytes = StreamUtil.readAllBytes(is);
            String string = new String(bytes, StandardCharsets.UTF_8);
            JsonObject ob = gson.fromJson(string, JsonObject.class);
            lang = new Lang(ob);
        } catch (Exception e) {
            e.printStackTrace();
            lang = new Lang(new JsonObject());
        }
    }

    private void loadDynmap() {
        if (mainConfig.integrations().map().enabled()) {
            logger.log(Level.INFO, lang.get("dynmap-check"));
            MapService.get();
        }
    }

    private void loadTrustedPlayers() {
        logger.log(Level.INFO, lang.get("trust-load"));
        trustFile = new File(dataFolder, "trust.yml");
        trustConfig = new YamlConfiguration();
        try {
            trustConfig.load(trustFile);
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            logger.log(Level.WARNING, lang.get("trust-load-err"));
            e.printStackTrace();
        }
    }

    private PlatformSchedulerTask autosaveTask = null;
    private boolean performedAnyLoad = false;
    private void loadClaims() {
        if (this.autosaveTask != null) {
            this.autosaveTask.cancel();
        }
        try {
            logger.log(Level.INFO, lang.get("claims-load"));
            claimsFile = new File(dataFolder, "claims.yml");
            claimsConfig = new YamlConfiguration();
            try {
                claimsConfig.load(claimsFile);
            } catch (FileNotFoundException ignored) {
            } catch (Exception e) {
                logger.log(Level.WARNING, lang.get("claims-load-err"));
                e.printStackTrace();
            }
            logger.log(Level.INFO, lang.get("claims-unpack"));
            for (String key : claimsConfig.getKeys(false)) {
                ConfigurationSection section = claimsConfig.getConfigurationSection(key);
                if (section == null) {
                    logger.log(Level.WARNING, lang.get("claims-unpack-err", key, lang.get("claims-unpack-err-section")));
                    continue;
                }
                Claim claim;
                try {
                    claim = Claim.deserialize(section);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    logger.log(Level.WARNING, lang.get("claims-unpack-err", key, e.getClass().getSimpleName()));
                    continue;
                }
                claim.claim();
            }
            if (mainConfig.autoSave().enabled()) {
                double interval = mainConfig.autoSave().interval();
                if (interval > 0.0) {
                    long intervalTicks = Math.round(interval * 20d);
                    this.autosaveTask = Platform.get().getScheduler().runTaskTimer(this, this::autoSaveClaims, 0L, intervalTicks);
                }
            }
        } finally {
            this.performedAnyLoad = true;
        }
    }

    private void startServices() {
        // bStats
        Metrics metrics = new Metrics(this, 16129);
        // Placeholders
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                Class<?> clazz = Class.forName("io.github.wasabithumb.xclaim.placeholder.XClaimPlaceholderExpansion");
                Constructor<?> con = clazz.getConstructor(XClaim.class);
                Object expansion = con.newInstance(this);
                Method registerMethod = clazz.getMethod("register");
                registerMethod.invoke(expansion);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to bind placeholders", e);
            }
        }
        //
        // TODO: Add "starting GUI service" debug message
        gui = GuiService.create(mainConfig.gui().version());
        gui.start();
        logger.log(Level.INFO, lang.get("services-chunk-editor"));
        ChunkEditor.initialize();
        logger.log(Level.INFO, lang.get("services-command"));
        OfflinePlayerType.initializeListener();
        commandManager = new CommandManager();
        logger.log(Level.INFO, lang.get("services-command-register"));
        commandManager.registerDefaults();
        logger.log(Level.INFO, lang.get("services-movement"));
        MovementRoutine.initialize();
        logger.log(Level.INFO, lang.get("services-grace"));
        GraceRoutine.refresh();
    }

    private void checkForUpdates() {
        Platform.get().getScheduler().runTaskAsynchronously(this, () -> {
            String option = UpdateCommand.initialCheck();
            if (option == null) return;
            this.broadcastConsoleOps(
                    lang.getComponent("update-available-line1", option),
                    lang.getComponent("update-available-line2")
            );
        });
    }

    private void warnConfigMigration() {
        this.broadcastConsoleOps(
                lang.getComponent("config-migration-line1"),
                lang.getComponent("config-migration-line2")
        );
    }

    private void broadcastConsoleOps(final @NotNull Component @NotNull ... messages) {
        Platform.get().getScheduler().synchronize(() -> {
            BukkitAudiences adventure = Platform.getAdventure();
            Audience au = adventure.console();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) {
                    au = Audience.audience(au, adventure.player(p));
                }
            }
            for (Component message : messages) au.sendMessage(message);
        });
    }

    private void saveTrustedPlayers() {
        logger.log(Level.INFO, lang.get("trust-save"));
        try {
            if (!trustFile.exists()) {
                if (trustFile.createNewFile()) {
                    logger.log(Level.INFO, lang.get("trust-save-new"));
                }
            }
            trustConfig.save(trustFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, lang.get("trust-save-err"));
            e.printStackTrace();
        }
    }

    private void autoSaveClaims() {
        this.saveClaims(getConfig().getBoolean("auto-save.silent", false));
    }

    private void saveClaims(boolean silent) {
        if (!this.performedAnyLoad) return;
        if (!silent) logger.log(Level.INFO, lang.get("claims-save"));
        Set<String> removeKeys = claimsConfig.getKeys(false);
        for (Claim claim : Claim.getAll()) {
            String token = claim.getUniqueToken();
            ConfigurationSection section = claimsConfig.getConfigurationSection(token);
            if (section == null) section = claimsConfig.createSection(token);
            //
            claim.serialize(section);
            removeKeys.remove(token);
        }
        for (String key : removeKeys) claimsConfig.set(key, null);
        try {
            if (!claimsFile.exists()) {
                if (claimsFile.createNewFile()) {
                    if (!silent) logger.log(Level.INFO, lang.get("claims-save-new"));
                }
            }
            claimsConfig.save(claimsFile);
        } catch (Exception e) {
            if (!silent) logger.log(Level.WARNING, lang.get("claims-save-err"), e);
        }
    }

    private void stopServices() {
        logger.log(Level.INFO, lang.get("services-stop"));
        if (mainConfig.editor().stopOnShutdown()) {
            for (Player ply : Bukkit.getOnlinePlayers()) ChunkEditor.stopEditing(ply);
        }
        commandManager.unregisterAll();
        OfflinePlayerType.clearListener();
        MovementRoutine.cleanup();
        GraceRoutine.stop();
        gui.stop();
        MapService.unload();
        if (this.autosaveTask != null) {
            this.autosaveTask.cancel();
        }
    }

    public static class Lang {

        private static final Pattern pattern = Pattern.compile("(\\$\\d+)");
        private static final MiniMessage mm = MiniMessage.miniMessage();
        private static final MiniMessage strict = MiniMessage
                .builder()
                .strict(true)
                .build();

        private final Map<String, String> map = new HashMap<>();

        Lang(JsonObject ob) {
            for (Map.Entry<String, JsonElement> entry : ob.entrySet()) {
                JsonElement value = entry.getValue();
                if (value.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) value;
                    if (primitive.isString()) {
                        map.put(entry.getKey(), primitive.getAsString());
                    }
                }
            }
        }

        private @Nullable String rawGet(String key) {
            return map.get(key);
        }

        public boolean has(String key) {
            return map.containsKey(key);
        }

        public String get(String key) {
            return get(key, new String[0]);
        }

        public String get(String key, String... args) {
            String base = rawGet(key);
            if (base != null) {
                StringBuilder out = new StringBuilder();
                StringBuilder term = null;
                boolean buildingTerm = false;
                for (char c : base.toCharArray()) {
                    if (buildingTerm) {
                        if (c >= '0' && c <= '9') {
                            term.append(c);
                        } else {
                            buildingTerm = false;
                            String brk = term.toString();
                            try {
                                int val = Integer.parseInt(brk);
                                if (val < 1 || val > args.length) {
                                    throw new IllegalArgumentException();
                                }
                                String arg = args[val - 1];
                                out.append(arg);
                            } catch (Exception e) {
                                out.append("$").append(term);
                            }
                            out.append(c);
                        }
                        continue;
                    }
                    if (c == '$') {
                        buildingTerm = true;
                        term = new StringBuilder();
                    } else {
                        out.append(c);
                    }
                }
                if (buildingTerm) {
                    String brk = term.toString();
                    try {
                        int val = Integer.parseInt(brk);
                        if (val < 1 || val > args.length) {
                            throw new IllegalArgumentException();
                        }
                        String arg = args[val - 1];
                        out.append(arg);
                    } catch (Exception e) {
                        out.append("$").append(term);
                    }
                }
                return out.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i=0; i < args.length; i++) {
                    if (i > 0) sb.append(" ");
                    sb.append(args[i]);
                }
                return sb.toString();
            }
        }

        public String get(String key, int... args) {
            String[] argStrings = new String[args.length];
            for (int i=0; i < args.length; i++) {
                argStrings[i] = String.valueOf(args[i]);
            }
            return get(key, argStrings);
        }

        public Component getComponent(String key) {
            String string = get(key);
            return mm.deserialize(string);
        }

        public Component getComponent(String key, Component... args) {
            String[] argStrings = new String[args.length];
            for (int i=0; i < args.length; i++) {
                argStrings[i] = strict.serializeOrNull(args[i]);
            }
            String string = get(key, argStrings);
            return mm.deserialize(string);
        }

        public Component getComponent(String key, String... args) {
            String string = get(key, args);
            return mm.deserialize(string);
        }

        public Component getComponent(String key, int... args) {
            String string = get(key, args);
            return mm.deserialize(string);
        }

    }

    @DebugGoal(async = true)
    @SuppressWarnings("unused")
    static void config(@NotNull DebugWriter writer) {
        writer.color(NamedTextColor.GOLD);
        writer.println("Impl: " + mainConfig.getClass().getName());
        writer.println("----------------");
        writer.println();

        config0(writer, mainConfig);
        writer.println();

        config1(writer, mainConfig.autoSave());
        writer.println();

        config2(writer, mainConfig.editor());
        writer.println();

        config3(writer, mainConfig.integrations());
        writer.println();

        config4(writer, mainConfig.rules());
        writer.println();

        config5(writer, mainConfig.worlds());
        writer.println();
    }

    private static void config0(@NotNull DebugWriter writer, @NotNull RootConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[root]");
        writer.color(NamedTextColor.WHITE);

        writer.println("language = " + cfg.language());
        writer.println("veteran-time = " + cfg.veteranTime());
        writer.println("no-paper-nag = " + cfg.noPaperNag());
    }

    private static void config1(@NotNull DebugWriter writer, @NotNull AutoSaveConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[auto-save]");
        writer.color(NamedTextColor.WHITE);

        writer.println("enabled = " + cfg.enabled());
        writer.println("debug = " + cfg.debug());
        writer.println("interval = " + cfg.interval());
        writer.println("silent = " + cfg.silent());
    }

    private static void config2(@NotNull DebugWriter writer, @NotNull EditorConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[editor]");
        writer.color(NamedTextColor.WHITE);

        writer.println("start-on-create = " + cfg.startOnCreate());
        writer.println("stop-on-shutdown = " + cfg.stopOnShutdown());
        writer.println("stop-on-leave = " + cfg.stopOnLeave());
    }

    private static void config3(@NotNull DebugWriter writer, @NotNull IntegrationsConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[integrations.economy]");
        writer.color(NamedTextColor.WHITE);
        config30(writer, cfg.economy());
        writer.println();

        writer.color(NamedTextColor.AQUA);
        writer.println("[integrations.map]");
        writer.color(NamedTextColor.WHITE);
        config31(writer, cfg.map());
        writer.println();

        writer.color(NamedTextColor.AQUA);
        writer.println("[integrations.protection]");
        writer.color(NamedTextColor.WHITE);
        config300(writer, cfg.protection());
    }

    private static void config300(@NotNull DebugWriter writer, @NotNull ToggleableConfig cfg) {
        writer.println("enabled = " + cfg.enabled());
        writer.println("debug = " + cfg.debug());
    }

    private static void config30(@NotNull DebugWriter writer, @NotNull EconomyConfig cfg) {
        config300(writer, cfg);
        writer.println("claim-price.default = " + cfg.claimPrice(null));
        writer.println("unclaim-reward.default = " + cfg.unclaimReward(null));
        writer.println("free-chunks.default = " + cfg.freeChunks(null));
    }

    private static void config31(@NotNull DebugWriter writer, @NotNull MapConfig cfg) {
        config300(writer, cfg);
        writer.println("old-outline-style = " + cfg.oldOutlineStyle());
    }

    private static void config4(@NotNull DebugWriter writer, @NotNull RulesConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[rules]");
        writer.color(NamedTextColor.WHITE);

        writer.println("placement = " + cfg.placementRaw() + " (" + cfg.placement().name() + ")");
        writer.println("min-distance = " + cfg.minDistance());
        writer.println("exempt-owner = " + cfg.exemptOwner());
        writer.println("max-chunks.default = " + cfg.maxChunks(null));
        writer.println("max-claims.default = " + cfg.maxClaims(null));
        writer.println("max-claims-in-world.default = " + cfg.maxClaimsInWorld(null));
    }

    private static void config5(@NotNull DebugWriter writer, @NotNull WorldsConfig cfg) {
        writer.color(NamedTextColor.AQUA);
        writer.println("[worlds]");
        writer.color(NamedTextColor.WHITE);

        writer.println("grace-time = " + cfg.graceTime());
        writer.println("use-whitelist = " + cfg.useWhitelist());
        writer.println("whitelist = " + Arrays.toString(cfg.whitelist().toArray()));
        writer.println("use-blacklist = " + cfg.useBlacklist());
        writer.println("whitelist = " + Arrays.toString(cfg.blacklist().toArray()));
        writer.println("case-sensitive = " + cfg.caseSensitive());
    }

     */

}
