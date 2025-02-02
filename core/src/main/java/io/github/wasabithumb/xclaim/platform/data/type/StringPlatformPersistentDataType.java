package io.github.wasabithumb.xclaim.platform.data.type;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.*;

@ApiStatus.Internal
final class StringPlatformPersistentDataType implements PlatformPersistentDataType<String> {

    public static final int ORDINAL = 0;

    //

    @Override
    public int ordinal() {
        return ORDINAL;
    }

    @Override
    public @NotNull Class<String> valueClass() {
        return String.class;
    }

    @Override
    public @NotNull String fallback() {
        return "";
    }

    @Override
    public void serialize(@NotNull OutputStream os, @NotNull String value) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(value);
        dos.flush();
    }

    @Override
    public @NotNull String deserialize(@NotNull InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        return dis.readUTF();
    }

}
