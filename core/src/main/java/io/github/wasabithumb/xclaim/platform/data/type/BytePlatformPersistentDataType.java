package io.github.wasabithumb.xclaim.platform.data.type;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ApiStatus.Internal
final class BytePlatformPersistentDataType implements PlatformPersistentDataType<Byte> {

    public static final int ORDINAL = 1;

    //

    @Override
    public int ordinal() {
        return ORDINAL;
    }

    @Override
    public @NotNull Class<Byte> valueClass() {
        return Byte.class;
    }

    @Override
    public @NotNull Byte fallback() {
        return (byte) 0;
    }

    @Override
    public void serialize(@NotNull OutputStream os, @NotNull Byte value) throws IOException {
        os.write(value.intValue());
    }

    @Override
    public @NotNull Byte deserialize(@NotNull InputStream is) throws IOException {
        int read = is.read();
        if (read == -1)
            throw new EOFException("Expected BYTE value, got EOF");
        return (byte) read;
    }

}
