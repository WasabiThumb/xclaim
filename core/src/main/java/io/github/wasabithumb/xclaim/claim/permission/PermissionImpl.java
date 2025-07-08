package io.github.wasabithumb.xclaim.claim.permission;

import io.github.wasabithumb.xclaim.i18n.Translatable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
final class PermissionImpl implements Permission {

    private static final Permission[] CANONICAL = new Permission[Permission.class.getDeclaredFields().length];
    private static int ORDINAL_HEAD = 0;

    @Contract("_, _ -> new")
    static synchronized @NotNull Permission create(@NotNull String name, @NotNull String @NotNull ... legacyNames) {
        final int ordinal = ORDINAL_HEAD++;
        Permission ret = new PermissionImpl(
                ordinal,
                name,
                legacyNames.length == 0 ?
                        Collections.emptyList() :
                        List.of(legacyNames)
        );
        CANONICAL[ordinal] = ret;
        return ret;
    }

    @Contract("-> new")
    static @NotNull Permission @NotNull [] values() {
        return Arrays.copyOf(CANONICAL, ORDINAL_HEAD);
    }

    static @NotNull Permission valueOf(int ordinal) throws IllegalArgumentException {
        if (ordinal < 0 || ordinal > ORDINAL_HEAD)
            throw new IllegalArgumentException("Illegal ordinal (" + ordinal + ")");
        return CANONICAL[ordinal];
    }

    static @NotNull Permission valueOf(@NotNull String name, boolean allowLegacy) throws IllegalArgumentException {
        Permission next;
        for (int i=0; i < ORDINAL_HEAD; i++) {
            next = CANONICAL[i];
            if (nameEquals(next.name(), name)) return next;
            if (allowLegacy) {
                for (String legacy : next.legacyNames()) {
                    if (nameEquals(legacy, name)) return next;
                }
            }
        }
        throw new IllegalArgumentException("\"" + name + "\" does not match any valid permission name");
    }

    static int count() {
        return ORDINAL_HEAD;
    }

    private static boolean nameEquals(@NotNull CharSequence a, @NotNull CharSequence b) {
        final int al = a.length();
        if (al != b.length()) return false;

        char ac, bc;
        for (int i=0; i < al; i++) {
            ac = a.charAt(i);
            bc = b.charAt(i);
            if ('A' <= bc && bc <= 'Z') {
                bc = ((char) (bc + 32));
            } else if (bc == '_') {
                bc = '-';
            }
            if (ac != bc) return false;
        }

        return true;
    }

    //

    private final int ordinal;
    private final String name;
    private final List<String> legacyNames;

    private PermissionImpl(int ordinal, @NotNull String name, @NotNull List<String> legacyNames) {
        this.ordinal = ordinal;
        this.name = name;
        this.legacyNames = legacyNames;
    }

    //

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull List<String> legacyNames() {
        return this.legacyNames;
    }

    @Override
    public @NotNull Translatable printName() {
        return Translatable.keyed("perm-" + this.name + "-name");
    }

    @Override
    public @NotNull Translatable description() {
        return Translatable.keyed("perm-" + this.name + "-description");
    }

    @Override
    public @NotNull TrustLevel defaultTrust() {
        // TODO: use configuration
        return TrustLevel.NONE;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.ordinal);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PermissionImpl other)) return false;
        return this.ordinal == other.ordinal;
    }

    @Override
    public @NotNull String toString() {
        return this.name;
    }

}
