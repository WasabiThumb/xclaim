package codes.wasabi.xclaim;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.MovementRoutine;
import codes.wasabi.xclaim.api.dynmap.DynmapInterfaceFactory;
import codes.wasabi.xclaim.command.CommandManager;
import codes.wasabi.xclaim.command.argument.type.OfflinePlayerType;
import codes.wasabi.xclaim.economy.Economy;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.StreamUtil;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class XClaim extends JavaPlugin {

    public static XClaim instance;
    public static Logger logger;
    public static File trustFile;
    public static YamlConfiguration trustConfig;
    public static File claimsFile;
    public static YamlConfiguration claimsConfig;
    public static FileConfiguration mainConfig;
    public static CommandManager commandManager;
    public static File jarFile;
    public static boolean hasDynmap = false;
    public static codes.wasabi.xclaim.api.dynmap.DynmapInterface dynmapInterface = null;
    public static File dataFolder;
    public static Lang lang;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        loadGeneralConfig();
        setupLang();
        if (!Economy.isAvailable()) {
            if (mainConfig.getBoolean("use-economy", false)) {
                logger.log(Level.WARNING, lang.get("eco-fail"));
            }
        }
        Platform.init();
        dataFolder = getDataFolder();
        if (dataFolder.mkdirs()) logger.log(Level.INFO, lang.get("data-folder-created"));
        locateJarFile();
        loadDynmap();
        loadTrustedPlayers();
        loadClaims();
        startServices();
        logger.log(Level.INFO, lang.get("startup-done"));
    }

    @Override
    public void onDisable() {
        saveTrustedPlayers();
        saveClaims();
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
        mainConfig = getConfig();
    }

    private static final String[] bundledLangs = new String[] {
            "en-US", "de"
    };
    private void setupLang() {
        String l = mainConfig.getString("language", "en-US");
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
                    curJson = gson.fromJson(new FileReader(bundledFile), JsonObject.class);
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
                try (InputStream is = Objects.requireNonNull(getResource("lang/" + bundled + ".json"))) {
                    JsonObject model = gson.fromJson(new InputStreamReader(is), JsonObject.class);
                    for (Map.Entry<String, JsonElement> entry : model.entrySet()) {
                        String key = entry.getKey();
                        if (!curJson.has(key)) curJson.add(key, entry.getValue());
                    }
                }
                try (OutputStream os = new FileOutputStream(bundledFile, false)) {
                    String json = gson.toJson(curJson);
                    os.write(json.getBytes(StandardCharsets.UTF_8));
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
        if (mainConfig.getBoolean("dynmap-integration.enabled", true)) {
            logger.log(Level.INFO, lang.get("dynmap-check"));
            dynmapInterface = DynmapInterfaceFactory.createElseNull();
            hasDynmap = dynmapInterface != null;
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

    private void loadClaims() {
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
    }

    private void startServices() {
        logger.log(Level.INFO, lang.get("services-chunk-editor"));
        ChunkEditor.initialize();
        logger.log(Level.INFO, lang.get("services-command"));
        OfflinePlayerType.initializeListener();
        commandManager = new CommandManager();
        logger.log(Level.INFO, lang.get("services-command-register"));
        commandManager.registerDefaults();
        logger.log(Level.INFO, lang.get("services-movement"));
        MovementRoutine.initialize();
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

    private void saveClaims() {
        logger.log(Level.INFO, lang.get("claims-save"));
        Set<String> removeKeys = claimsConfig.getKeys(false);
        for (Claim claim : Claim.getAll()) {
            String name = claim.getName();
            ConfigurationSection section = claimsConfig.getConfigurationSection(name);
            if (section == null) section = claimsConfig.createSection(name);
            //
            claim.serialize(section);
            removeKeys.remove(name);
        }
        for (String key : removeKeys) claimsConfig.set(key, null);
        try {
            if (!claimsFile.exists()) {
                if (claimsFile.createNewFile()) {
                    logger.log(Level.INFO, lang.get("claims-save-new"));
                }
            }
            claimsConfig.save(claimsFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, lang.get("claims-save-err"));
            e.printStackTrace();
        }
    }

    private void stopServices() {
        logger.log(Level.INFO, lang.get("services-stop"));
        if (mainConfig.getBoolean("stop-editing-on-shutdown", false)) {
            for (Player ply : Bukkit.getOnlinePlayers()) ChunkEditor.stopEditing(ply);
        }
        commandManager.unregisterAll();
        OfflinePlayerType.clearListener();
        MovementRoutine.cleanup();
        GUIHandler.closeAll();
        unloadDynmap();
    }

    private void unloadDynmap() {
        if (hasDynmap) {
            dynmapInterface.cleanup();
        }
        hasDynmap = false;
        dynmapInterface = null;
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

}
