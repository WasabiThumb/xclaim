package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformCustomInventory<D> extends BukkitPlatformCustomInventory<D> {

    public SpigotPlatformCustomInventory(@NotNull SpigotPlatform platform, int size, @NotNull String title, @NotNull D data) {
        super(platform, data);
        this.setHandle(Bukkit.createInventory(
                this,
                size,
                LegacyComponentSerializer.legacySection().serialize(
                        platform.mm().deserialize(title)
                )
        ));
    }

    @Override
    public @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) this.platform;
    }

}
