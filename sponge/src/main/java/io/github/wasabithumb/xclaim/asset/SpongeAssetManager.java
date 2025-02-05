package io.github.wasabithumb.xclaim.asset;

import io.github.wasabithumb.xclaim.asset.impl.DirectoryAssetSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpongeAssetManager extends AssetManager {

    private final Path dir;

    public SpongeAssetManager(@NotNull PluginContainer plugin, @NotNull ConfigManager config) {
        this.dir = config.pluginConfig(plugin).directory();
        this.setup();
    }

    @Override
    protected @NotNull DirectoryAssetSource createData() {
        try {
            if (!Files.isDirectory(this.dir))
                Files.createDirectories(this.dir);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return new DirectoryAssetSource(this.dir.toFile());
    }

}
