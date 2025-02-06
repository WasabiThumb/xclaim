package io.github.wasabithumb.xclaim.asset;

import io.github.wasabithumb.xclaim.asset.impl.DirectoryAssetSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AssetManager {

    private static final Pattern CODE_SOURCE_PATTERN = Pattern.compile(
            "^(?:file|jar)://((?:[^!]|(?<!\\.jar)!)+).*$",
            Pattern.CASE_INSENSITIVE
    );

    //

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
        URL location = AssetManager.class.getProtectionDomain().getCodeSource().getLocation();
        File codeSource;
        Matcher m = CODE_SOURCE_PATTERN.matcher(location.getPath());
        if (m.matches()) {
            codeSource = FileSystems.getDefault().getPath(m.group(1)).toAbsolutePath().toFile();
        } else {
            try {
                codeSource = new File(location.toURI());
            } catch (URISyntaxException e) {
                throw new AssertionError(e);
            }
        }
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
