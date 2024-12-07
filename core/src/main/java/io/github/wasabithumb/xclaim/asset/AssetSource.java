package io.github.wasabithumb.xclaim.asset;

import io.github.wasabithumb.xclaim.asset.impl.ArchiveAssetSource;
import io.github.wasabithumb.xclaim.asset.impl.DirectoryAssetSource;
import io.github.wasabithumb.xclaim.asset.impl.HybridAssetSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface AssetSource {

    static @NotNull ArchiveAssetSource archive(@NotNull File file) {
        return new ArchiveAssetSource(file);
    }

    static @NotNull DirectoryAssetSource directory(@NotNull File dir) {
        return new DirectoryAssetSource(dir);
    }

    static @NotNull HybridAssetSource.Builder hybrid() {
        return HybridAssetSource.builder();
    }

    //

    @NotNull AssetSource sub(@NotNull AssetPath path);

    default @NotNull AssetSource sub(@NotNull String path) {
        return this.sub(AssetPath.parse(path));
    }

    @NotNull List<String> list(boolean includeDirs, boolean includeFiles) throws IOException;

    default @NotNull List<String> list() throws IOException {
        return this.list(true, true);
    }

    boolean exists(@NotNull AssetPath path) throws IOException;

    default boolean exists(@NotNull String path) throws IOException {
        return this.exists(AssetPath.parse(path));
    }

    @NotNull InputStream read(@NotNull AssetPath path) throws IOException;

    default @NotNull InputStream read(@NotNull String path) throws IOException {
        return this.read(AssetPath.parse(path));
    }

    default @NotNull OutputStream write(@NotNull AssetPath path) throws UnsupportedOperationException, IOException {
        throw new UnsupportedOperationException("Source does not support write");
    }

    default @NotNull OutputStream write(@NotNull String path) throws UnsupportedOperationException, IOException {
        return this.write(AssetPath.parse(path));
    }

}
