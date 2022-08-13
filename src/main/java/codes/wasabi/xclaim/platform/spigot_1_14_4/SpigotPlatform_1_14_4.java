package codes.wasabi.xclaim.platform.spigot_1_14_4;

import codes.wasabi.xclaim.platform.spigot_1_14.SpigotPlatform_1_14;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class SpigotPlatform_1_14_4 extends SpigotPlatform_1_14 {

    @Override
    public void createExplosion(World w, Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
        w.createExplosion(loc, power, setFire, breakBlocks, source);
    }

}
