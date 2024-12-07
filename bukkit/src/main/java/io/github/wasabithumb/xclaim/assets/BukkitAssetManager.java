package io.github.wasabithumb.xclaim.assets;

import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import io.github.wasabithumb.xclaim.asset.impl.DirectoryAssetSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BukkitAssetManager extends AssetManager {

    private final JavaPlugin plugin;
    public BukkitAssetManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.setup();
    }

    @Override
    protected @NotNull DirectoryAssetSource createData() {
        File dataDir = this.plugin.getDataFolder();
        if (!dataDir.isDirectory() && !dataDir.mkdirs())
            throw new AssertionError("Failed to create new directory \"" + dataDir.getAbsolutePath() + "\"");
        return AssetSource.directory(dataDir);
    }

}
