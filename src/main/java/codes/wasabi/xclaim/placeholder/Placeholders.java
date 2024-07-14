package codes.wasabi.xclaim.placeholder;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.placeholder.impl.*;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public final class Placeholders {

    public static final Placeholder CLAIM_COUNT = Placeholder.simple(
            "claim_count",
            (OfflinePlayer o) -> Integer.toString(Claim.getByOwner(o).size())
    );

    public static final Placeholder CLAIM_COUNT_IN_WORLD = new CountInWorldPlaceholder(false);

    public static final Placeholder CLAIM_MAX = Placeholder.simple(
            "claim_max",
            (OfflinePlayer o) -> Integer.toString(XCPlayer.of(o).getMaxClaims())
    );

    public static final Placeholder CHUNK_COUNT = Placeholder.simple(
            "chunk_count",
            (OfflinePlayer o) -> Integer.toString(Claim.getByOwner(o).stream().mapToInt((Claim c) -> c.getChunks().size()).sum())
    );

    public static final Placeholder CHUNK_COUNT_IN_WORLD = new CountInWorldPlaceholder(true);

    public static final Placeholder CHUNK_MAX = Placeholder.simple(
            "chunk_max",
            (OfflinePlayer o) -> Integer.toString(XCPlayer.of(o).getMaxChunks())
    );

    public static final Placeholder CHUNK_ABS_MAX = Placeholder.simple(
            "chunk_max_abs",
            (OfflinePlayer o) -> Integer.toString(XCPlayer.of(o).getMaxChunks() * XCPlayer.of(o).getMaxClaims())
    );

    //

    public static @NotNull Placeholder @NotNull [] values() {
        final Field[] declaredFields = Placeholders.class.getDeclaredFields();

        final Placeholder[] ret = new Placeholder[declaredFields.length];
        int i = 0;

        Placeholder value;
        for (Field f : declaredFields) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            if (!Modifier.isPublic(f.getModifiers())) continue;
            if (!Placeholder.class.isAssignableFrom(f.getType())) continue;

            try {
                value = (Placeholder) f.get(null);
            } catch (ReflectiveOperationException | SecurityException e) {
                XClaim.logger.log(Level.WARNING, "Unexpected error while enumerating placeholders", e);
                continue;
            }

            ret[i++] = value;
        }

        if (i < declaredFields.length) {
            Placeholder[] cpy = new Placeholder[i];
            System.arraycopy(ret, 0, cpy, 0, i);
            return cpy;
        }
        return ret;
    }

}
