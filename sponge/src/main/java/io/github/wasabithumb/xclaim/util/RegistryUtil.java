package io.github.wasabithumb.xclaim.util;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.registry.DefaultedRegistryType;
import org.spongepowered.api.registry.DefaultedRegistryValue;
import org.spongepowered.api.registry.RegistryReference;

public final class RegistryUtil {

    public static <T extends DefaultedRegistryValue> boolean referenceEquals(
            @NotNull DefaultedRegistryType<T> type,
            @NotNull T value,
            @NotNull RegistryReference<T> reference
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
