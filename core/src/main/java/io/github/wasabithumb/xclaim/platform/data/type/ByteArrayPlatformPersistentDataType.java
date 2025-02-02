package io.github.wasabithumb.xclaim.platform.data.type;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ApiStatus.Internal
final class ByteArrayPlatformPersistentDataType implements PlatformPersistentDataType<byte[]> {

    public static final int ORDINAL = 2;

    //

    @Override
    public int ordinal() {
        return ORDINAL;
    }

    @Override
    public @NotNull Class<byte[]> valueClass() {
        return byte[].class;
    }

    @Override
    public byte @NotNull [] fallback() {
        return new byte[0];
    }

    @Override
    public void serialize(@NotNull OutputStream os, byte @NotNull [] value) throws IOException {
        int len = value.length;
        os.write(new byte[] {
                (byte) (len >> 24),
                (byte) ((len >> 16) & 0xFF),
                (byte) ((len >> 8) & 0xFF),
                (byte) (len & 0xFF)
        });
        os.write(value);
    }

    @Override
    public byte @NotNull [] deserialize(@NotNull InputStream is) throws IOException {
        byte[] lb = is.readNBytes(4);
        int len = ((lb[0] & 0xFF) << 24) |
                ((lb[1] & 0xFF) << 16) |
                ((lb[2] & 0xFF) << 8) |
                (lb[3] & 0xFF);
        return is.readNBytes(len);
    }

}
