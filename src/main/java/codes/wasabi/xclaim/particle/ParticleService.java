package codes.wasabi.xclaim.particle;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.particle.legacy.LegacyParticleService;
import codes.wasabi.xclaim.particle.legacy.LegacyParticleServiceReflection;
import codes.wasabi.xclaim.particle.spigot.SpigotParticleService;
import io.papermc.lib.PaperLib;
import org.apache.commons.io.IOUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;

public abstract class ParticleService {

    private static boolean INIT = false;
    private static ParticleService INSTANCE = null;
    public static void init() {
        if (!INIT) {
            try {
                if (PaperLib.isVersion(20)) {
                    if (PaperLib.isPaper()) {
                        INSTANCE = new codes.wasabi.xclaim.particle.paper.PaperParticleService();
                    } else {
                        INSTANCE = new SpigotParticleService();
                    }
                } else {
                    LegacyParticleServiceReflection reflect = loadParticleLib();
                    INSTANCE = new LegacyParticleService(reflect);
                }
            } catch (Exception e) {
                XClaim.logger.log(Level.WARNING, "Failed to create preferred particle service", e);
                INSTANCE = new SpigotParticleService();
            }
        }
        INIT = true;
    }

    public static ParticleService get() {
        init();
        return INSTANCE;
    }

    private static final String PLIB_PACKAGE = new String(new char[] {
            'x', 'y', 'z', '.', 'x', 'e', 'n', 'o', 'n', 'd', 'e', 'v', 's', '.', 'p', 'a', 'r', 't', 'i', 'c', 'l', 'e'
    });
    private static final String PLIB_URI = "https://repo1.maven.org/maven2/xyz/xenondevs/particle/1.8.4/particle-1.8.4.jar";
    private static LegacyParticleServiceReflection loadParticleLib() throws Exception {
        File destDir = new File(XClaim.dataFolder, "lib");
        if (!destDir.exists()) destDir.mkdirs();

        File destFile = new File(destDir, "particle.jar");
        if (!destFile.exists()) {
            XClaim.logger.info(XClaim.lang.get("ext-dl")); // Fetching required libraries...

            try (OutputStream os = new FileOutputStream(destFile, false)) {
                URL url = new URL(PLIB_URI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/java-archive");
                conn.setRequestProperty("User-Agent", "XClaim; wasabithumbs@gmail.com");
                conn.setDoInput(true);
                conn.setDoOutput(false);

                try (InputStream is = conn.getInputStream()) {
                    IOUtils.copyLarge(is, os);
                }
            }
        }

        URLClassLoader cl = new URLClassLoader(new URL[] { destFile.toURI().toURL() }, XClaim.class.getClassLoader());
        Class<?> builderClass = Class.forName(PLIB_PACKAGE + ".ParticleBuilder", true, cl);
        Class<?> effectClass = Class.forName(PLIB_PACKAGE + ".ParticleEffect", true, cl);
        Constructor<?> builderConstructor = builderClass.getConstructor(effectClass);
        Method builderSetColor = builderClass.getMethod("setColor", Color.class);
        Method builderSetLocation = builderClass.getMethod("setLocation", Location.class);
        Method builderSetAmount = builderClass.getMethod("setAmount", int.class);
        Method builderSetOffset = builderClass.getMethod("setOffset", float.class, float.class, float.class);
        Method builderDisplay = builderClass.getMethod("display", Player[].class);
        return new LegacyParticleServiceReflection(
                builderClass, effectClass, builderConstructor,
                builderSetColor, builderSetLocation, builderSetAmount, builderSetOffset, builderDisplay
        );
    }

    //

    public abstract void displayRedstoneParticle(Color color, Location pos, int amount, float offsetX, float offsetY, float offsetZ, Player player);

}
