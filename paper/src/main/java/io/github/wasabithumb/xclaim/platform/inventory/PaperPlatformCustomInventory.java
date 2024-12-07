package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformCustomInventory<D> extends BukkitPlatformCustomInventory<D> {

    public PaperPlatformCustomInventory(@NotNull PaperPlatform platform, int size, @NotNull String title, @NotNull D data) {
        super(platform, data);
        this.setHandle(Bukkit.createInventory(
                this,
                size,
                platform.mm().deserialize(title)
        ));
    }

    @Override
    public @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

}
