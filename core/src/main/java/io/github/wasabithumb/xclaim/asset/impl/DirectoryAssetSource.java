package io.github.wasabithumb.xclaim.asset.impl;

import io.github.wasabithumb.xclaim.asset.AssetPath;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryAssetSource implements AssetSource {

    private final File dir;
    public DirectoryAssetSource(@NotNull File dir) {
        if (!dir.isDirectory())
            throw new IllegalArgumentException("Path \"" + dir + "\" is not a directory");
        this.dir = dir;
    }

    //

    public @NotNull File resolve(@NotNull String path) {
        return this.resolve(AssetPath.parse(path));
    }

    public @NotNull File resolve(@NotNull AssetPath path) {
        return this.resolve(this.dir, path);
    }

    private @NotNull File resolve(@NotNull File file, @NotNull AssetPath path) {
        for (CharSequence part : path.parts()) {
            file = new File(file, part.toString());
        }
        return file;
    }

    @Override
    public @NotNull DirectoryAssetSource sub(@NotNull AssetPath path) {
        File target = this.resolve(this.dir, path);
        if (!target.isDirectory()) {
            try {
                Files.createDirectories(target.toPath());
            } catch (IOException e) {
                throw new AssertionError("Failed to create new directory @ " + target.getAbsolutePath(), e);
            }
        }
        return new DirectoryAssetSource(target);
    }

    @Override
    public @NotNull List<String> list(boolean includeDirs, boolean includeFiles) throws IOException {
        if (!this.dir.isDirectory()) return Collections.emptyList();
        File[] files = this.dir.listFiles();
        if (files == null) return Collections.emptyList();
        List<String> ret = new ArrayList<>(files.length);

        for (File f : files) {
            if (f.isDirectory()) {
                if (includeDirs) ret.add(f.getName());
            } else if (f.isFile()) {
                if (includeFiles) ret.add(f.getName());
            }
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public boolean exists(@NotNull AssetPath path) throws IOException {
        File file = this.resolve(this.dir, path);
        return file.isFile() || file.isDirectory();
    }

    @Override
    public @NotNull InputStream read(@NotNull AssetPath path) throws IOException {
        File file = this.resolve(this.dir, path);
        return new FileInputStream(file);
    }

    @Override
    public @NotNull OutputStream write(@NotNull AssetPath path) throws IOException {
        File file = this.resolve(this.dir, path);
        File parent = file.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs())
            throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
        return new FileOutputStream(file);
    }

}
