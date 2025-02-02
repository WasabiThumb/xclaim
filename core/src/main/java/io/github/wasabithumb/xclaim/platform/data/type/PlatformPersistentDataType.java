package io.github.wasabithumb.xclaim.platform.data.type;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ApiStatus.NonExtendable
public sealed interface PlatformPersistentDataType<T> permits
        StringPlatformPersistentDataType,
        BytePlatformPersistentDataType,
        ByteArrayPlatformPersistentDataType,
        LongPlatformPersistentDataType
{

    PlatformPersistentDataType<String> STRING     = new StringPlatformPersistentDataType();
    PlatformPersistentDataType<Byte> BYTE         = new BytePlatformPersistentDataType();
    PlatformPersistentDataType<byte[]> BYTE_ARRAY = new ByteArrayPlatformPersistentDataType();
    PlatformPersistentDataType<Long> LONG         = new LongPlatformPersistentDataType();

    @ApiStatus.Internal
    static @NotNull PlatformPersistentDataType<?> valueOf(int ordinal) {
        return switch (ordinal) {
            case StringPlatformPersistentDataType.ORDINAL    -> STRING;
            case BytePlatformPersistentDataType.ORDINAL      -> BYTE;
            case ByteArrayPlatformPersistentDataType.ORDINAL -> BYTE_ARRAY;
            case LongPlatformPersistentDataType.ORDINAL      -> LONG;
            default -> throw new IllegalArgumentException("Invalid data type ordinal (" + ordinal + ")");
        };
    }

    //

    @ApiStatus.Internal
    int ordinal();

    @ApiStatus.Internal
    @NotNull Class<T> valueClass();

    @NotNull T fallback();

    void serialize(@NotNull OutputStream os, @NotNull T value) throws IOException;

    @NotNull T deserialize(@NotNull InputStream is) throws IOException;

}
