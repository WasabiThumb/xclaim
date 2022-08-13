package codes.wasabi.xclaim.platform.spigot_1_12_2;

import codes.wasabi.xclaim.platform.spigot_1_12.SpigotPlatform_1_12;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

public class SpigotPlatform_1_12_2 extends SpigotPlatform_1_12 {

    @Override
    public void setOwningPlayer(SkullMeta sm, OfflinePlayer player) {
        sm.setOwningPlayer(player);
    }

    @Override
    public boolean bukkitTaskCancelled(BukkitTask task) {
        return task.isCancelled();
    }

}
