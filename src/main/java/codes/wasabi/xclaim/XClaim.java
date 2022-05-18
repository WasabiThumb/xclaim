package codes.wasabi.xclaim;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.MovementRoutine;
import codes.wasabi.xclaim.api.dynmap.DynmapInterfaceFactory;
import codes.wasabi.xclaim.command.CommandManager;
import codes.wasabi.xclaim.command.argument.type.OfflinePlayerType;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        dataFolder = getDataFolder();
        if (dataFolder.mkdirs()) logger.log(Level.INFO, "Created data folder");
        locateJarFile();
        loadGeneralConfig();
        loadDynmap();
        loadTrustedPlayers();
        loadClaims();
        startServices();
        logger.log(Level.INFO, "Done");
    }

    @Override
    public void onDisable() {
        saveTrustedPlayers();
        saveClaims();
        stopServices();
        logger.log(Level.INFO, "Done");
    }

    /* BEGIN STARTUP TASKS */
    private void locateJarFile() {
        logger.log(Level.INFO, "Locating JAR file");
        jarFile = new File(XClaim.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    private void loadGeneralConfig() {
        logger.log(Level.INFO, "Loading general config");
        saveDefaultConfig();
        mainConfig = getConfig();
    }

    private void loadDynmap() {
        if (mainConfig.getBoolean("dynmap-integration.enabled", true)) {
            logger.log(Level.INFO, "Checking for Dynmap...");
            dynmapInterface = DynmapInterfaceFactory.createElseNull();
            hasDynmap = dynmapInterface != null;
        }
    }

    private void loadTrustedPlayers() {
        logger.log(Level.INFO, "Loading trusted players");
        trustFile = new File(dataFolder, "trust.yml");
        trustConfig = new YamlConfiguration();
        try {
            trustConfig.load(trustFile);
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occurred while loading trusted players. See details below.");
            e.printStackTrace();
        }
    }

    private void loadClaims() {
        logger.log(Level.INFO, "Loading claims");
        claimsFile = new File(dataFolder, "claims.yml");
        claimsConfig = new YamlConfiguration();
        try {
            claimsConfig.load(claimsFile);
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occured while loading claims. See details below.");
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Unpacking...");
        for (String key : claimsConfig.getKeys(false)) {
            ConfigurationSection section = claimsConfig.getConfigurationSection(key);
            if (section == null) {
                logger.log(Level.WARNING, "Claim \"" + key + "\" is corrupt (Reason: Not a section). Skipping...");
                continue;
            }
            Claim claim;
            try {
                claim = Claim.deserialize(section);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Claim \"" + key + "\" is corrupt (Reason: " + e.getClass().getSimpleName() + "). Skipping, see details below.");
                e.printStackTrace();
                continue;
            }
            claim.claim();
        }
    }

    private void startServices() {
        logger.log(Level.INFO, "Initializing chunk editor");
        ChunkEditor.initialize();
        logger.log(Level.INFO, "Loading command manager");
        OfflinePlayerType.initializeListener();
        commandManager = new CommandManager();
        logger.log(Level.INFO, "Registering default commands");
        commandManager.registerDefaults();
        logger.log(Level.INFO, "Starting movement routine");
        MovementRoutine.initialize();
    }
    /* END STARTUP TASKS */

    /* BEGIN SHUTDOWN TASKS */
    private void saveTrustedPlayers() {
        logger.log(Level.INFO, "Saving trusted players");
        try {
            if (!trustFile.exists()) {
                if (trustFile.createNewFile()) {
                    logger.log(Level.INFO, "Created new trust.yml");
                }
            }
            trustConfig.save(trustFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occurred while saving trusted players. See details below.");
            e.printStackTrace();
        }
    }

    private void saveClaims() {
        logger.log(Level.INFO, "Saving claims");
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
                    logger.log(Level.INFO, "Created new claims.yml");
                }
            }
            claimsConfig.save(claimsFile);
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occurred while saving claims. See details below.");
            e.printStackTrace();
        }
    }

    private void stopServices() {
        logger.log(Level.INFO, "Stopping services");
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

}
