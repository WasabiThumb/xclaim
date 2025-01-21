package io.github.wasabithumb.xclaim.gui.layout;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.config.sub.GuiConfig;
import io.github.wasabithumb.xclaim.gui.layout.xml.XmlGuiLayout;
import io.github.wasabithumb.xclaim.util.io.stream.StreamUtil;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.StampedLock;

public class GuiLayouts {

    private final XClaim runtime;
    private LoadStage stage = LoadStage.IDLE;
    private final StampedLock stageLock = new StampedLock();
    private Throwable fatal = null;
    private final Object endPreEntriesSignal = new Object();
    protected LinkedList<LoadEntry> entries = new LinkedList<>();
    private Map<String, LoadResult> map = new HashMap<>();

    @ApiStatus.Internal
    public GuiLayouts(@NotNull XClaim runtime) {
        this.runtime = runtime;
    }

    //

    public @Nullable GuiLayout get(@NotNull String name) {
        LoadResult lr = null;

        long stamp = this.stageLock.readLock();
        try {
            switch (this.stage) {
                case IDLE:
                    // Loading was never started; this will kick us into PRE_ENTRIES
                    stamp = this.startLoadingInternal(stamp);
                    stamp = this.stageLock.tryConvertToReadLock(stamp);
                case PRE_ENTRIES:
                    // The directory listing has not come back yet, so we need to block to be able to tell the caller
                    // whether the provided name has a matching file or not.
                    synchronized (this.endPreEntriesSignal) {
                        this.stageLock.unlock(stamp);
                        try {
                            this.endPreEntriesSignal.wait();
                        } catch (InterruptedException e) {
                            throw new AssertionError(
                                    "Awaiting entry resolution for \"" + name + "\" was interrupted",
                                    e
                            );
                        }
                        stamp = this.stageLock.readLock();
                    }
                    if (this.stage == LoadStage.FATAL) this.throwFatal();
                    // Assume that we are now POST_ENTRIES, since we are not FATAL
                case POST_ENTRIES:
                    // The directory listing has come back, but not all the files have been processed yet.
                    // Find the requested entry and resolve when it is processed, irrespective of whether *every* file
                    // has been processed.
                    for (LoadEntry le : this.entries) {
                        if (name.equals(le.name)) {
                            lr = LoadResult.ofFuture(le.value, true);
                        }
                    }
                    break;
                case FATAL:
                    // The loading process has failed in a way that would affect all entries.
                    this.throwFatal();
                    return null;
                case DONE:
                    // Every entry has been resolved. Use a map instead of iteration.
                    lr = this.map.get(name);
                    break;
            }
        } finally {
            this.stageLock.unlock(stamp);
        }

        if (lr == null) return null;
        if (lr.success()) {
            return lr.value();
        } else {
            throw new AssertionError("Failed to load data for layout \"" + name + "\"", lr.error());
        }
    }

    public void startLoading() {
        long stamp = this.stageLock.readLock();
        try {
            if (this.stage == LoadStage.IDLE) {
                stamp = this.startLoadingInternal(stamp);
            }
        } finally {
            this.stageLock.unlock(stamp);
        }
    }

    // REQUIRES READ LOCK!
    private long startLoadingInternal(long stamp) {
        stamp = this.stageLock.tryConvertToWriteLock(stamp);
        LoadWorker worker = new LoadWorker(this);
        this.stage = LoadStage.PRE_ENTRIES;
        worker.start();
        return stamp;
    }

    protected void setPostEntries() {
        long stamp = this.stageLock.writeLock();
        try {
            this.stage = LoadStage.POST_ENTRIES;
            synchronized (this.endPreEntriesSignal) {
                this.endPreEntriesSignal.notify();
            }
        } finally {
            this.stageLock.unlock(stamp);
        }
    }

    protected void setFatal(@NotNull Throwable err) {
        long stamp = this.stageLock.writeLock();
        try {
            this.stage = LoadStage.FATAL;
            this.fatal = err;
            synchronized (this.endPreEntriesSignal) {
                this.endPreEntriesSignal.notify();
            }
        } finally {
            this.stageLock.unlock(stamp);
        }
    }

    @Contract(" -> fail")
    private void throwFatal() {
        throw new AssertionError("Layout loader threw a fatal exception", this.fatal);
    }

    protected void transferEntries() {
        long stamp = this.stageLock.writeLock();
        try {
            LoadEntry entry;
            while ((entry = this.entries.pollLast()) != null) {
                this.map.put(entry.name, LoadResult.ofFuture(entry.value, false));
            }

            this.map = Collections.unmodifiableMap(this.map);
            this.stage = LoadStage.DONE;
        } finally {
            this.stageLock.unlock(stamp);
        }
    }

    //

    protected enum LoadStage {
        IDLE,
        FATAL,
        PRE_ENTRIES,
        POST_ENTRIES,
        DONE,
    }

    protected interface LoadResult {

        static @NotNull LoadResult success(@NotNull GuiLayout value) {
            return new Success(value);
        }

        static @NotNull LoadResult error(@NotNull Throwable cause) {
            return new Error(cause);
        }

        static @NotNull LoadResult ofFuture(@NotNull CompletableFuture<GuiLayout> future, boolean wait) {
            try {
                final GuiLayout value = wait ? future.get() : future.getNow(null);
                if (value != null) {
                    return LoadResult.success(value);
                } else {
                    return LoadResult.error(
                            new IllegalStateException("Result was unwrapped before resolution")
                    );
                }
            } catch (CompletionException | ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause == null) cause = e;
                return LoadResult.error(cause);
            } catch (InterruptedException e) {
                return LoadResult.error(e);
            }
        }

        //

        boolean success();

        @UnknownNullability GuiLayout value();

        @UnknownNullability Throwable error();

        //

        class Success implements LoadResult {

            private final GuiLayout value;
            Success(@NotNull GuiLayout value) {
                this.value = value;
            }

            @Override
            @Contract(" -> true")
            public boolean success() {
                return true;
            }

            @Override
            public @NotNull GuiLayout value() {
                return this.value;
            }

            @Override
            @Contract(" -> null")
            public Throwable error() {
                return null;
            }

        }

        class Error implements LoadResult {

            private final Throwable error;
            Error(@NotNull Throwable error) {
                this.error = error;
            }

            @Override
            @Contract(" -> false")
            public boolean success() {
                return false;
            }

            @Override
            @Contract(" -> null")
            public GuiLayout value() {
                return null;
            }

            @Override
            public @NotNull Throwable error() {
                return this.error;
            }

        }

    }

    protected static class LoadEntry {

        final String name;
        final CompletableFuture<GuiLayout> value = new CompletableFuture<>();
        LoadEntry(@NotNull String name) {
            this.name = name;
        }

    }

    protected static class LoadWorker extends Thread {

        final GuiLayouts parent;
        LoadWorker(@NotNull GuiLayouts parent) {
            super("XClaim GUI Layout Loader");
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                this.populateEntries();
            } catch (IOException e) {
                this.parent.setFatal(e);
                return;
            }
            this.parent.setPostEntries();

            GuiLayout layout;
            for (LoadEntry le : this.parent.entries) {
                try {
                    layout = this.resolveEntry(le);
                    le.value.complete(layout);
                } catch (IOException e) {
                    le.value.completeExceptionally(e);
                }
            }
            this.parent.transferEntries();
        }

        private @NotNull AssetManager assets() {
            return this.parent.runtime.assets();
        }

        private void populateEntries() throws IOException {
            final File layoutsFolder = this.assets().data().resolve("layouts");

            if ((!layoutsFolder.isDirectory()) && (!layoutsFolder.mkdirs())) {
                throw new IOException("Failed to create new directory: " + layoutsFolder.getAbsolutePath());
            }

            List<String> files = this.assets()
                    .resources()
                    .sub("layouts")
                    .list(false, true);

            LoadEntry entry;
            for (String file : files) {
                if (!file.endsWith(".xml")) continue;
                entry = new LoadEntry(file.substring(0, file.length() - 4));
                this.parent.entries.add(entry);
            }
        }

        private @NotNull GuiLayout resolveEntry(@NotNull LoadEntry entry) throws IOException {
            final String path = "layouts/" + entry.name + ".xml";

            if (!this.assets().data().exists(path)) {
                try (InputStream is = this.assets().resources().read(path);
                     OutputStream os = this.assets().data().write(path)
                ) {
                    StreamUtil.pipe(is, os);
                }
            }

            try (InputStream is = this.assets().data().read(path)) {
                GuiConfig cfg = this.parent.runtime.rootConfig().gui();
                XmlGuiLayout ret = new XmlGuiLayout(cfg.height());
                ret.setDefaultBasis(cfg.basis());
                ret.read(is);
                return ret;
            }
        }

    }

}
