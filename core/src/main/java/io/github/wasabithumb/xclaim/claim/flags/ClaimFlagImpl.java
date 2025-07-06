package io.github.wasabithumb.xclaim.claim.flags;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

@ApiStatus.Internal
final class ClaimFlagImpl implements ClaimFlag {

    static final int MAGIC_MIN = 'f';
    static final int MAGIC_MAX = 'w';
    static final ClaimFlagImpl[] BY_MAGIC = new ClaimFlagImpl[MAGIC_MAX - MAGIC_MIN + 1];

    static int SHIFT_HEAD = 0;
    static final int SHIFT_MAX = Math.min(31, ClaimFlag.class.getDeclaredFields().length);
    static final ClaimFlagImpl[] BY_SHIFT = new ClaimFlagImpl[SHIFT_MAX + 1];

    @Contract("_, _ -> new")
    static @NotNull ClaimFlag create(
            @NotNull String name,
            @Range(from=MAGIC_MIN, to=MAGIC_MAX) int magic
    ) {
        final int shift = SHIFT_HEAD++;
        ClaimFlagImpl ret = new ClaimFlagImpl(name, magic, shift);
        BY_MAGIC[magic - MAGIC_MIN] = ret;
        BY_SHIFT[shift] = ret;
        return ret;
    }

    static @NotNull ClaimFlag byChar(int c) throws IllegalArgumentException {
        if (MAGIC_MIN <= c && c <= MAGIC_MAX) {
            ClaimFlagImpl ret = BY_MAGIC[c - MAGIC_MIN];
            if (ret != null) return ret;
        }
        throw new IllegalArgumentException("Invalid flag character: " + c);
    }

    static @NotNull ClaimFlag byShift(int shift) throws IllegalArgumentException {
        if (0 <= shift && shift <= SHIFT_MAX) return BY_SHIFT[shift];
        throw new IllegalArgumentException("No flag with shift of " + shift);
    }

    static @NotNull ClaimFlag @NotNull [] values() {
        return Arrays.copyOf(BY_SHIFT, SHIFT_HEAD);
    }

    //

    private final char magic;
    private final String name;
    private final byte shift;

    private ClaimFlagImpl(
            @NotNull String name,
            int magic,
            int shift
    ) {
        if (MAGIC_MIN > magic || magic > MAGIC_MAX)
            throw new IllegalArgumentException("Illegal magic character");
        if (0 > shift || shift > SHIFT_MAX)
            throw new IllegalArgumentException("Illegal shift value");

        this.magic = (char) magic;
        this.name = name;
        this.shift = (byte) shift;
    }

    //

    @Override
    public char magic() {
        return this.magic;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public int value() {
        return 1 << Byte.toUnsignedInt(this.shift);
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(this.shift);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClaimFlagImpl other)) return false;
        return this.shift == other.shift;
    }

    @Override
    public @NotNull String toString() {
        return this.name;
    }

}
