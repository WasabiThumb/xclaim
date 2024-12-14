package io.github.wasabithumb.xclaim.asset;

import io.github.wasabithumb.xclaim.asset.impl.DirectoryAssetSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class AssetManager {

    protected DirectoryAssetSource data;
    protected AssetSource resources;
    protected AssetSource omni;

    protected final void setup() {
        this.data = this.createData();
        this.resources = this.createResources();
        this.omni = this.createOmni();
    }

    protected abstract @NotNull DirectoryAssetSource createData();

    protected @NotNull AssetSource createResources() {
        File codeSource = new File(AssetManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        // TODO: This seems to cause problems with Windows! We probably shouldn't bother handling .paper-remapped,
        // as when the migration is complete, the paper version should have a paper-plugin.yml and hint to Paper
        // not to remap this plugin. This would keep this method clear of any platform-specific hacks.
        return AssetSource.archive(codeSource);
    }

    protected @NotNull AssetSource createOmni() {
        return AssetSource.hybrid()
                .flags("rwl")
                .source(this.data)
                .flags("rl")
                .source(this.resources)
                .build();
    }

    /**
     * Provides an AssetSource representing the data directory
     */
    public @NotNull DirectoryAssetSource data() {
        return this.data;
    }

    /**
     * Provides an AssetSource representing the plugin JAR
     */
    public @NotNull AssetSource resources() {
        return this.resources;
    }

    /**
     * Provides an AssetSource that reads from either the data directory or plugin JAR, and always writes to the
     * data directory.
     */
    public @NotNull AssetSource omni() {
        return this.omni;
    }

}
