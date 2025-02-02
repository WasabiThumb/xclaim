package io.github.wasabithumb.xclaim.platform.data.type;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ApiStatus.Internal
final class LongPlatformPersistentDataType implements PlatformPersistentDataType<Long> {

    public static final int ORDINAL = 3;

    //

    @Override
    public int ordinal() {
        return ORDINAL;
    }

    @Override
    public @NotNull Class<Long> valueClass() {
        return Long.class;
    }

    @Override
    public @NotNull Long fallback() {
        return 0L;
    }

    @Override
    public void serialize(@NotNull OutputStream os, @NotNull Long value) throws IOException {
        for (int z=56; z >= 0; z -= 8) {
            os.write((int) ((value >> z) & 0xFFL));
        }
    }

    @Override
    public @NotNull Long deserialize(@NotNull InputStream is) throws IOException {
        long ret = 0L;
        int read;
        for (int i=0; i < 8; i++) {
            ret <<= 8;
            read = is.read();
            if (read == -1) throw new EOFException("Expected LONG value, got EOF (" + i + " / 8)");
            ret |= read;
        }
        return ret;
    }

}
