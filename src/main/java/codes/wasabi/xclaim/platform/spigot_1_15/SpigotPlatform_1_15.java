package codes.wasabi.xclaim.platform.spigot_1_15;

import codes.wasabi.xclaim.platform.spigot_1_14.SpigotPlatform_1_14;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotPlatform_1_15 extends SpigotPlatform_1_14 {

    @Override
    public boolean supportsArtificialBookOpen() {
        return true;
    }

    @Override
    public void artificialBookOpen(Player ply, ItemStack book) {
        ply.openBook(book);
    }

}
