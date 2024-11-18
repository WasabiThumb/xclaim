package io.github.wasabithumb.xclaim.platform.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public sealed interface PlatformPersistentDataType<T> {

    PlatformPersistentDataType<String> STRING     = new Impl<>(0, String.class, "");
    PlatformPersistentDataType<Byte> BYTE         = new Impl<>(1, Byte.class, (byte) 0);
    PlatformPersistentDataType<byte[]> BYTE_ARRAY = new Impl<>(2, byte[].class, new byte[0]);

    //

    @ApiStatus.Internal
    int ordinal();

    @ApiStatus.Internal
    @NotNull Class<T> valueClass();

    @NotNull T fallback();

    //

    final class Impl<Q> implements PlatformPersistentDataType<Q> {

        private final int ordinal;
        private final Class<Q> valueClass;
        private final Q fallback;
        Impl(int ordinal, @NotNull Class<Q> valueClass, @NotNull Q fallback) {
            this.ordinal = ordinal;
            this.valueClass = valueClass;
            this.fallback = fallback;
        }

        @Override
        public int ordinal() {
            return this.ordinal;
        }

        @Override
        public @NotNull Class<Q> valueClass() {
            return this.valueClass;
        }

        @Override
        public @NotNull Q fallback() {
            return this.fallback;
        }

    }

}
