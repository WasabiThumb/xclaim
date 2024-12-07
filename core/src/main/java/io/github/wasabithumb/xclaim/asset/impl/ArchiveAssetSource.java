package io.github.wasabithumb.xclaim.asset.impl;

import io.github.wasabithumb.xclaim.asset.AssetPath;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import io.github.wasabithumb.xclaim.util.io.stream.StreamUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ArchiveAssetSource implements AssetSource {

    private final File file;
    private final String root;

    protected ArchiveAssetSource(@NotNull File file, @NotNull String root) {
        this.file = file;
        this.root = root;
    }

    public ArchiveAssetSource(@NotNull File file) {
        this(file, "");
    }

    //

    @Override
    public @NotNull ArchiveAssetSource sub(@NotNull AssetPath path) {
        if (this.root.isEmpty()) {
            return new ArchiveAssetSource(this.file, path + "/");
        } else {
            return new ArchiveAssetSource(this.file, this.root + path);
        }
    }

    @Override
    public @NotNull List<String> list(boolean includeDirs, boolean includeFiles) throws IOException {
        List<String> ret = new LinkedList<>();
        Pattern p = Pattern.compile("^" + Pattern.quote(this.root) + "([^/]+)(/?)$");
        try (FileInputStream fis = new FileInputStream(this.file);
             ZipInputStream zis = new ZipInputStream(fis)
        ) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                Matcher m = p.matcher(ze.getName());
                if (!m.matches()) continue;
                boolean isDir = m.groupCount() > 1 && !m.group(2).isEmpty();
                if (isDir ? includeDirs : includeFiles)
                    ret.add(m.group(1));
            }
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public boolean exists(@NotNull AssetPath path) throws IOException {
        try (ZipFile zf = new ZipFile(this.file)) {
            return zf.getEntry(path.toString()) != null;
        }
    }

    @Override
    public @NotNull InputStream read(@NotNull AssetPath path) throws IOException {
        boolean close = true;
        ZipFile zf = null;
        try {
            zf = new ZipFile(this.file);
            ZipEntry ze = zf.getEntry(this.root + path);
            if (ze == null) {
                throw new IOException("Path \"" + this.root + path +
                        "\" does not exist in archive (" + this.file.getAbsolutePath() + ")");
            }
            InputStream ret = zf.getInputStream(ze);
            close = false;
            return StreamUtil.closeListener(ret, zf);
        } finally {
            if (close && zf != null) zf.close();
        }
    }

}
