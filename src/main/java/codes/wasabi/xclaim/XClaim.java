package codes.wasabi.xclaim;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.GraceRoutine;
import codes.wasabi.xclaim.api.MovementRoutine;
import codes.wasabi.xclaim.command.CommandManager;
import codes.wasabi.xclaim.command.argument.type.OfflinePlayerType;
import codes.wasabi.xclaim.command.sub.UpdateCommand;
import codes.wasabi.xclaim.config.impl.defaulting.DefaultingRootConfig;
import codes.wasabi.xclaim.config.impl.yaml.YamlRootConfig;
import codes.wasabi.xclaim.config.struct.RootConfig;
import codes.wasabi.xclaim.config.struct.helpers.ToggleableConfig;
import codes.wasabi.xclaim.config.struct.sub.*;
import codes.wasabi.xclaim.config.struct.sub.integrations.EconomyConfig;
import codes.wasabi.xclaim.config.struct.sub.integrations.MapConfig;
import codes.wasabi.xclaim.debug.Debuggable;
import codes.wasabi.xclaim.debug.goal.DebugGoal;
import codes.wasabi.xclaim.debug.writer.DebugWriter;
import codes.wasabi.xclaim.economy.Economy;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.particle.ParticleService;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import codes.wasabi.xclaim.util.StreamUtil;
import com.google.gson.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FileUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Debuggable
public final class XClaim extends JavaPlugin {

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

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        loadGeneralConfig();
        setupLang();
        if (!Economy.isAvailable()) {
            if (mainConfig.integrations().economy().enabled()) {
                logger.log(Level.WARNING, lang.get("eco-fail"));
            }
        }
        Platform.init();
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

    /* BEGIN STARTUP TASKS */
    private void locateJarFile() {
        logger.log(Level.INFO, lang.get("locating-jar"));
        jarFile = new File(XClaim.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    private void loadGeneralConfig() {
        saveDefaultConfig();
        mainConfig = new DefaultingRootConfig(new YamlRootConfig(this.getConfig()));
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
                Class<?> clazz = Class.forName("codes.wasabi.xclaim.placeholder.XClaimPlaceholderExpansion");
                Constructor<?> con = clazz.getConstructor(XClaim.class);
                Object expansion = con.newInstance(this);
                Method registerMethod = clazz.getMethod("register");
                registerMethod.invoke(expansion);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to bind placeholders", e);
            }
        }
        //
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
            BukkitAudiences adventure = Platform.getAdventure();
            Audience au = adventure.console();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) {
                    au = Audience.audience(au, adventure.player(p));
                }
            }
            au.sendMessage(lang.getComponent("update-available-line1", option));
            au.sendMessage(lang.getComponent("update-available-line2"));
        });
    }
    /* END STARTUP TASKS */

    /* BEGIN SHUTDOWN TASKS */
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
        GUIHandler.closeAll();
        MapService.unload();
        if (this.autosaveTask != null) {
            this.autosaveTask.cancel();
        }
    }
    /* END SHUTDOWN TASKS */

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

    /* START Debug */

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

    /* END Debug */

}
