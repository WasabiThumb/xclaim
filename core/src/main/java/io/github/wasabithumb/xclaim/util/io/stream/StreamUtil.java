package io.github.wasabithumb.xclaim.util.io.stream;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public final class StreamUtil {

    @Contract("_, null -> param1; _, !null -> new")
    public static @NotNull InputStream closeListener(@NotNull InputStream stream, @Nullable Closeable attachment) {
        if (attachment == null) return stream;
        return new CloseListenerInputStream(stream, attachment);
    }

    @Contract("_, null -> param1; _, !null -> new")
    public static @NotNull InputStream tee(@NotNull InputStream stream, @Nullable OutputStream out) {
        if (out == null) return stream;
        return new TeeInputStream(stream, out);
    }

    @Contract("_, null -> param1; _, !null -> new")
    public static @NotNull InputStream tee(@NotNull InputStream stream, @Nullable File out) throws FileNotFoundException {
        if (out == null) return stream;
        return tee(stream, new FileOutputStream(out, false));
    }

    @SuppressWarnings("UnusedReturnValue")
    public static long pipe(@NotNull InputStream src, @NotNull OutputStream dest) throws IOException {
        return pipe(src, dest, 8192);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static long pipe(@NotNull InputStream src, @NotNull OutputStream dest, final int bufSize) throws IOException {
        byte[] buf = new byte[bufSize];
        long total = 0L;
        int read;
        while ((read = src.read(buf, 0, bufSize)) != -1) {
            total += read;
            dest.write(buf, 0, read);
        }
        dest.flush();
        return total;
    }

}
