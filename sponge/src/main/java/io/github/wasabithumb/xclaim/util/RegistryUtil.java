package io.github.wasabithumb.xclaim.util;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.*;

import java.lang.reflect.Field;

public final class RegistryUtil {

    public static <T> DefaultedRegistryReference<T> cataloguedReference(
            @NotNull RegistryType<T> type,
            @NotNull Class<?> catalogClass,
            @NotNull String @NotNull ... names
    ) {
        if (names.length == 0)
            throw new IllegalArgumentException("Must provide at least 1 name");

        NoSuchFieldException e = null;
        for (String name : names) {
            Field f;
            try {
                f = catalogClass.getDeclaredField(name);
            } catch (NoSuchFieldException e1) {
                if (e != null) e1.addSuppressed(e);
                e = e1;
                continue;
            }

            DefaultedRegistryReference<?> ref;
            try {
                ref = (DefaultedRegistryReference<?>) f.get(null);
            } catch (ReflectiveOperationException e1) {
                throw new AssertionError(e1);
            }
            return type.referenced(ref.location()).asDefaultedReference(Sponge::game);
        }

        throw new AssertionError("None of the provided names could be found in the catalog", e);
    }

    public static <T extends DefaultedRegistryValue> boolean cataloguedReferenceEquals(
            @NotNull DefaultedRegistryType<T> type,
            @NotNull RegistryReference<T> reference,
            @NotNull Class<?> catalogClass,
            @NotNull String @NotNull ... names
    ) {
        return referenceEquals(reference, cataloguedReference(type, catalogClass, names));
    }

    public static <T extends DefaultedRegistryValue> boolean referenceEquals(
            @NotNull DefaultedRegistryType<T> type,
            @NotNull T value,
            @NotNull RegistryReference<? extends T> reference
    ) {
        return referenceEquals(
                value.asDefaultedReference(type),
                reference
        );
    }

    public static boolean referenceEquals(
            @NotNull RegistryReference<?> a,
            @NotNull RegistryReference<?> b
    ) {
        if (!keyEquals(a.registry().location(), b.registry().location())) return false;
        return keyEquals(a.location(), b.location());
    }

    public static boolean keyEquals(
            @NotNull Key a,
            @NotNull Key b
    ) {
        if (a.equals(b)) return true;
        return a.namespace().equals(b.namespace()) &&
                a.value().equals(b.value());
    }

}
