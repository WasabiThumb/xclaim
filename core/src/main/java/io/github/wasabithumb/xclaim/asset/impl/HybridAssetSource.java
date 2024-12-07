package io.github.wasabithumb.xclaim.asset.impl;

import io.github.wasabithumb.xclaim.asset.AssetPath;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class HybridAssetSource implements AssetSource {

    protected static final int READ  = 0b001;
    protected static final int WRITE = 0b010;
    protected static final int LIST  = 0b100;

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    //

    protected final List<Entry> entries;
    protected HybridAssetSource(@NotNull List<Entry> entries) {
        this.entries = entries;
    }

    protected @NotNull Iterator<AssetSource> sources(final int flag) {
        return this.entries.stream()
                .filter((Entry e) -> (e.flags & flag) == flag)
                .map(Entry::source)
                .iterator();
    }

    @Override
    public @NotNull HybridAssetSource sub(@NotNull AssetPath path) {
        final int size = this.entries.size();
        Entry[] sub = new Entry[size];
        for (int i=0; i < size; i++) {
            Entry entry = this.entries.get(i);
            sub[i] = new Entry(
                    entry.source.sub(path),
                    entry.flags
            );
        }
        return new HybridAssetSource(Arrays.asList(sub));
    }

    @Override
    public @NotNull List<String> list(boolean includeDirs, boolean includeFiles) throws IOException {
        List<String> ret = Collections.emptyList();
        List<String> tmp;
        int mode = 0;

        for (Iterator<AssetSource> it = this.sources(LIST); it.hasNext(); ) {
            AssetSource source = it.next();
            tmp = source.list(includeDirs, includeFiles);
            switch (mode) {
                case 0:
                    ret = tmp;
                    mode = 1;
                    break;
                case 1:
                    List<String> cpy = new ArrayList<>(ret);
                    cpy.addAll(tmp);
                    ret = cpy;
                    mode = 2;
                    break;
                case 2:
                    ret.addAll(tmp);
                    break;
            }
        }

        return Collections.unmodifiableList(ret);
    }

    @Override
    public boolean exists(@NotNull AssetPath path) throws IOException {
        for (Iterator<AssetSource> it = this.sources(READ); it.hasNext(); ) {
            AssetSource source = it.next();
            if (source.exists(path)) return true;
        }
        return false;
    }

    @Override
    public @NotNull InputStream read(@NotNull AssetPath path) throws IOException {
        for (Iterator<AssetSource> it = this.sources(READ); it.hasNext(); ) {
            AssetSource source = it.next();
            if (source.exists(path))
                return source.read(path);
        }
        throw new IOException("No candidate to read \"" + path + "\"");
    }

    @Override
    public @NotNull OutputStream write(@NotNull AssetPath path) throws UnsupportedOperationException, IOException {
        Iterator<AssetSource> it = this.sources(WRITE);
        if (!it.hasNext())
            throw new IOException("No candidate to write \"" + path + "\"");
        return it.next().write(path);
    }

    //

    protected record Entry(@NotNull AssetSource source, int flags) { }

    public static final class Builder {

        private final List<Entry> entries = new LinkedList<>();
        private int flags = 0;

        public @NotNull Builder flags(@NotNull @Pattern("[rwl]{0,3}") String flags) {
            int f = 0;
            for (int i=0; i < flags.length(); i++) {
                switch (flags.charAt(i)) {
                    case 'r':
                        f |= READ;
                        break;
                    case 'w':
                        f |= WRITE;
                        break;
                    case 'l':
                        f |= LIST;
                        break;
                }
            }
            this.flags = f;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder source(@NotNull AssetSource source) {
            this.entries.add(new Entry(source, this.flags));
            return this;
        }

        @Contract("-> new")
        public @NotNull HybridAssetSource build() {
            return new HybridAssetSource(this.entries);
        }

    }

}
