package codes.wasabi.xclaim.util;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigUtil {

    public static boolean worldIsAllowed(FileConfiguration cfg, World world) {
        String worldName = world.getName();
        boolean cs = cfg.getBoolean("worlds.case-sensitive", true);
        boolean black = cfg.getBoolean("worlds.use-blacklist", false);
        if (black) {
            for (String name : cfg.getStringList("worlds.blacklist")) {
                if (cs ? name.equals(worldName) : name.equalsIgnoreCase(worldName)) return false;
            }
        }
        boolean white = cfg.getBoolean("worlds.use-whitelist", false);
        if (white) {
            for (String name : cfg.getStringList("worlds.whitelist")) {
                if (cs ? name.equals(worldName) : name.equalsIgnoreCase(worldName)) return true;
            }
            return false;
        }
        return true;
    }

}
