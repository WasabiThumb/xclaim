package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlatformItem extends PlatformObject {

    @NotNull PlatformMaterial type();

    int amount();

    @NotNull String displayName();

    @Contract("_ -> this")
    PlatformItem displayName(@NotNull String displayName);

    @NotNull
    List<String> lore();

    @Contract("_ -> this")
    PlatformItem lore(@NotNull List<String> lore);

    @Contract(" -> this")
    PlatformItem hideExtra();

}
