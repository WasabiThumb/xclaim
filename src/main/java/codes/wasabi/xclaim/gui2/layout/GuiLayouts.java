package codes.wasabi.xclaim.gui2.layout;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.layout.xml.XmlGuiLayout;
import codes.wasabi.xclaim.util.io.CloseListenerInputStream;
import codes.wasabi.xclaim.util.io.TeeInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class GuiLayouts {

    private static final Pattern FILE_PATTERN = Pattern.compile("^layouts/([a-z\\-]+)\\.xml$");

    private LoadStage stage = LoadStage.IDLE;
    private final StampedLock stageLock = new StampedLock();
    private Throwable fatal = null;
    private final Object endPreEntriesSignal = new Object();
    protected LinkedList<LoadEntry> entries = new LinkedList<>();
    private Map<String, LoadResult> map = new HashMap<>();

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
        final String bundledPath;
        final File diskPath;
        final CompletableFuture<GuiLayout> value = new CompletableFuture<>();
        LoadEntry(@NotNull String name, @NotNull String bundledPath, @NotNull File diskPath) {
            this.name = name;
            this.bundledPath = bundledPath;
            this.diskPath = diskPath;
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

        private void populateEntries() throws IOException {
            final File dataFolder = XClaim.instance.getDataFolder();
            final File layoutsFolder = new File(dataFolder, "layouts");

            if ((!layoutsFolder.isDirectory()) && (!layoutsFolder.mkdirs())) {
                throw new IOException("Failed to create new directory: " + layoutsFolder.getAbsolutePath());
            }

            try (FileInputStream fis = new FileInputStream(XClaim.jarFile);
                 ZipInputStream zis = new ZipInputStream(fis)
            ) {
                ZipEntry ze;
                Matcher m;
                LoadEntry entry;
                while ((ze = zis.getNextEntry()) != null) {
                    m = FILE_PATTERN.matcher(ze.getName());
                    if (!m.matches()) continue;

                    entry = new LoadEntry(
                            m.group(1),
                            ze.getName(),
                            new File(layoutsFolder, m.group(1) + ".xml")
                    );
                    this.parent.entries.add(entry);
                }
            }
        }

        private @NotNull GuiLayout resolveEntry(@NotNull LoadEntry entry) throws IOException {
            final File diskPath = entry.diskPath;

            InputStream is;
            if (diskPath.isFile()) {
                //noinspection IOStreamConstructor
                is = new FileInputStream(diskPath);
            } else {
                boolean close = true;
                ZipFile zf = null;
                is = null;
                try {
                    zf = new ZipFile(XClaim.jarFile);
                    ZipEntry ze = zf.getEntry(entry.bundledPath);
                    if (ze == null) throw new IOException("Entry \"" + entry.bundledPath + "\" no longer exists");
                    is = zf.getInputStream(ze);
                    is = new TeeInputStream(is, new FileOutputStream(diskPath, false));
                    is = new CloseListenerInputStream(is, zf);
                    close = false;
                } finally {
                    if (close) {
                        if (zf != null) zf.close();
                        if (is != null) is.close();
                    }
                }
            }

            try {
                XmlGuiLayout ret = new XmlGuiLayout(XClaim.mainConfig.gui().height());
                ret.setDefaultBasis(XClaim.mainConfig.gui().basis());
                ret.read(is);
                return ret;
            } finally {
                is.close();
            }
        }

    }

}
